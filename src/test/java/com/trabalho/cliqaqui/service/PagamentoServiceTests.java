package com.trabalho.cliqaqui.service;

import com.trabalho.cliqaqui.dto.CartaoCreditoDTO;
import com.trabalho.cliqaqui.model.*;
import com.trabalho.cliqaqui.repositories.PagamentoRepository;
import com.trabalho.cliqaqui.repositories.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagamentoServiceTests {

    @Mock
    private PagamentoRepository pagamentoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PagamentoService pagamentoService;

    private Pedido pedido;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNome("Test Cliente");

        pedido = new Pedido();
        pedido.setId(100);
        pedido.setCliente(cliente);
        pedido.setValorTotal(150.00);
        pedido.setStatus(StatusPedido.PENDENTE); // Initial status
    }

    @Test
    void testProcessarPagamentoCartao_Aprovado() {
        CartaoCreditoDTO cartaoDto = new CartaoCreditoDTO();
        cartaoDto.setNumeroCartao("4000000000000000"); // Visa
        cartaoDto.setNomeTitular("Test Holder");
        cartaoDto.setMesValidade("12");
        cartaoDto.setAnoValidade("2025");
        cartaoDto.setCvv("123");
        cartaoDto.setNumeroParcelas(1);

        when(pedidoRepository.findById(100)).thenReturn(Optional.of(pedido));
        when(pagamentoRepository.save(any(PagamentoCartao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PagamentoCartao resultado = pagamentoService.processarPagamentoCartao(100, cartaoDto);

        assertNotNull(resultado);
        assertEquals(StatusPagamento.APROVADO, resultado.getStatus());
        assertEquals("VISA", resultado.getBandeiraCartao());
        assertEquals("0000", resultado.getUltimosQuatroDigitos());
        assertEquals(pedido.getValorTotal(), resultado.getValor());
        assertNotNull(resultado.getNumeroCartaoToken());

        assertEquals(StatusPedido.PROCESSANDO, pedido.getStatus()); // Check if Pedido status updated
        assertEquals(resultado, pedido.getPagamento()); // Check if Pagamento linked to Pedido

        verify(pagamentoRepository, times(1)).save(any(PagamentoCartao.class));
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void testProcessarPagamentoCartao_Recusado() {
        CartaoCreditoDTO cartaoDto = new CartaoCreditoDTO();
        cartaoDto.setNumeroCartao("5000000000001111"); // Ends in 1111 for simulated refusal
        cartaoDto.setNomeTitular("Test Holder Refused");
        cartaoDto.setMesValidade("10");
        cartaoDto.setAnoValidade("2026");
        cartaoDto.setCvv("456");
        cartaoDto.setNumeroParcelas(1);

        when(pedidoRepository.findById(100)).thenReturn(Optional.of(pedido));
        when(pagamentoRepository.save(any(PagamentoCartao.class))).thenAnswer(invocation -> invocation.getArgument(0));
        // PedidoRepository.save will also be called
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));


        PagamentoCartao resultado = pagamentoService.processarPagamentoCartao(100, cartaoDto);

        assertNotNull(resultado);
        assertEquals(StatusPagamento.RECUSADO, resultado.getStatus());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());
        assertEquals(resultado, pedido.getPagamento());

        verify(pagamentoRepository, times(1)).save(any(PagamentoCartao.class));
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void testProcessarPagamentoBoleto() {
        when(pedidoRepository.findById(100)).thenReturn(Optional.of(pedido));
        when(pagamentoRepository.save(any(PagamentoBoleto.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PagamentoBoleto resultado = pagamentoService.processarPagamentoBoleto(100);

        assertNotNull(resultado);
        assertEquals(StatusPagamento.PENDENTE, resultado.getStatus());
        assertEquals(pedido.getValorTotal(), resultado.getValor());
        assertNotNull(resultado.getCodigoBarras());
        assertNotNull(resultado.getUrlBoleto());
        assertNotNull(resultado.getDataVencimento());
        assertTrue(resultado.getDataVencimento().after(new Date())); // Vencimento should be in the future

        assertEquals(StatusPedido.PENDENTE, pedido.getStatus());
        assertEquals(resultado, pedido.getPagamento());

        verify(pagamentoRepository, times(1)).save(any(PagamentoBoleto.class));
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void testConfirmarPagamento() {
        PagamentoBoleto pagamentoPendente = new PagamentoBoleto();
        pagamentoPendente.setId(200);
        pagamentoPendente.setPedido(pedido); // Link back to the pedido for the test
        pagamentoPendente.setStatus(StatusPagamento.PENDENTE);
        pagamentoPendente.setValor(pedido.getValorTotal());

        pedido.setPagamento(pagamentoPendente); // Assume it was set before
        pedido.setStatus(StatusPedido.PENDENTE);


        when(pagamentoRepository.findById(200)).thenReturn(Optional.of(pagamentoPendente));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pagamento resultado = pagamentoService.confirmarPagamento(200);

        assertNotNull(resultado);
        assertEquals(StatusPagamento.APROVADO, resultado.getStatus());
        assertEquals(StatusPedido.PROCESSANDO, pedido.getStatus()); // Check if Pedido status updated

        verify(pagamentoRepository, times(1)).save(pagamentoPendente);
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void testCancelarOuFalharPagamento() {
        PagamentoCartao pagamentoPendente = new PagamentoCartao();
        pagamentoPendente.setId(201);
        pagamentoPendente.setPedido(pedido);
        pagamentoPendente.setStatus(StatusPagamento.PENDENTE); // Or some other status before failure
        pagamentoPendente.setValor(pedido.getValorTotal());

        pedido.setPagamento(pagamentoPendente);
        pedido.setStatus(StatusPedido.PENDENTE);

        when(pagamentoRepository.findById(201)).thenReturn(Optional.of(pagamentoPendente));
        when(pagamentoRepository.save(any(Pagamento.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Pagamento resultado = pagamentoService.cancelarOuFalharPagamento(201);

        assertNotNull(resultado);
        assertEquals(StatusPagamento.RECUSADO, resultado.getStatus());
        assertEquals(StatusPedido.CANCELADO, pedido.getStatus());

        verify(pagamentoRepository, times(1)).save(pagamentoPendente);
        verify(pedidoRepository, times(1)).save(pedido);
    }

    @Test
    void testProcessarPagamentoCartao_PedidoNaoEncontrado() {
        when(pedidoRepository.findById(999)).thenReturn(Optional.empty());
        CartaoCreditoDTO cartaoDto = new CartaoCreditoDTO(); // DTO content doesn't matter here

        Exception exception = assertThrows(RuntimeException.class, () -> {
            pagamentoService.processarPagamentoCartao(999, cartaoDto);
        });
        assertEquals("Pedido não encontrado: 999", exception.getMessage());
        verify(pagamentoRepository, never()).save(any());
        verify(pedidoRepository, never()).save(any());
    }

}
