package com.trabalho.cliqaqui.controller;

import com.trabalho.cliqaqui.dto.CartaoCreditoDTO;
import com.trabalho.cliqaqui.model.*;
import com.trabalho.cliqaqui.repositories.PedidoRepository; // For mocking
import com.trabalho.cliqaqui.service.PagamentoService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString; // Required for content().string(containsString(...))


@WebMvcTest(PagamentoController.class)
public class PagamentoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagamentoService pagamentoService;

    @MockBean
    private PedidoRepository pedidoRepository; // Also mocked as it's used in PagamentoController

    private Pedido pedido;
    private Cliente cliente;
    private MockHttpSession mockSession;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setId(1);
        cliente.setNome("Test Cliente");
        cliente.setEmail("cliente@example.com");

        pedido = new Pedido();
        pedido.setId(100);
        pedido.setCliente(cliente);
        pedido.setValorTotal(150.00);
        pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setDataRealizacao(Timestamp.from(Instant.now()));

        // Mock session attributes
        mockSession = new MockHttpSession();
        mockSession.setAttribute("loggedInUserId", cliente.getId());
        mockSession.setAttribute("loggedInUserEmail", cliente.getEmail());
        mockSession.setAttribute("loggedInUserName", cliente.getNome());
        mockSession.setAttribute("loggedInUserType", "CLIENTE");
    }

    @Test
    void testEscolherFormaPagamento_Success() throws Exception {
        when(pedidoRepository.findById(100)).thenReturn(Optional.of(pedido));

        mockMvc.perform(get("/pagamento/pedido/100/escolher").session(mockSession))
            .andExpect(status().isOk())
            .andExpect(view().name("checkout/escolher-pagamento"))
            .andExpect(model().attributeExists("pedido"))
            .andExpect(model().attributeExists("cartaoCreditoDTO"));
    }

    @Test
    void testEscolherFormaPagamento_NotLoggedIn() throws Exception {
        mockMvc.perform(get("/pagamento/pedido/100/escolher")) // No session
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login"))
            .andExpect(flash().attribute("errorMessage", "Você precisa estar logado para efetuar o pagamento."));
    }

    @Test
    void testEscolherFormaPagamento_PedidoNotFound() throws Exception {
        when(pedidoRepository.findById(999)).thenReturn(Optional.empty());
        mockMvc.perform(get("/pagamento/pedido/999/escolher").session(mockSession))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/cliente/pedidos"))
            .andExpect(flash().attribute("errorMessage", "Pedido não encontrado."));
    }

    @Test
    void testProcessarPagamentoCartao_Success() throws Exception {
        PagamentoCartao pagamentoCartaoAprovado = new PagamentoCartao();
        pagamentoCartaoAprovado.setStatus(StatusPagamento.APROVADO);

        when(pedidoRepository.findById(100)).thenReturn(Optional.of(pedido)); // For initial checks in controller
        when(pagamentoService.processarPagamentoCartao(eq(100), any(CartaoCreditoDTO.class)))
            .thenReturn(pagamentoCartaoAprovado);

        mockMvc.perform(post("/pagamento/pedido/100/cartao").session(mockSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("numeroCartao", "1234567890123456")
                .param("nomeTitular", "Test User")
                .param("mesValidade", "12")
                .param("anoValidade", "2025")
                .param("cvv", "123")
                .param("numeroParcelas", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/checkout/order/100/confirmation"))
            .andExpect(flash().attribute("successMessage", "Pagamento com cartão aprovado!"));
    }

    @Test
    void testProcessarPagamentoCartao_Recusado() throws Exception {
        PagamentoCartao pagamentoCartaoRecusado = new PagamentoCartao();
        pagamentoCartaoRecusado.setStatus(StatusPagamento.RECUSADO);

        when(pedidoRepository.findById(100)).thenReturn(Optional.of(pedido));
        when(pagamentoService.processarPagamentoCartao(eq(100), any(CartaoCreditoDTO.class)))
            .thenReturn(pagamentoCartaoRecusado);

        mockMvc.perform(post("/pagamento/pedido/100/cartao").session(mockSession)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("numeroCartao", "1234567890123456")
                // other params...
                .param("nomeTitular", "Test User")
                .param("mesValidade", "12")
                .param("anoValidade", "2025")
                .param("cvv", "123")
                .param("numeroParcelas", "1"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/pagamento/pedido/100/escolher"))
            .andExpect(flash().attribute("errorMessage", "Pagamento com cartão recusado. Verifique os dados ou tente outra forma de pagamento."));
    }

    @Test
    void testProcessarPagamentoBoleto_Success() throws Exception {
        PagamentoBoleto pagamentoBoletoGerado = new PagamentoBoleto();
        pagamentoBoletoGerado.setCodigoBarras("12345678901234567890");
        pagamentoBoletoGerado.setUrlBoleto("http://simulado.com/boleto/123");
        pagamentoBoletoGerado.setDataVencimento(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow

        when(pedidoRepository.findById(100)).thenReturn(Optional.of(pedido));
        when(pagamentoService.processarPagamentoBoleto(100)).thenReturn(pagamentoBoletoGerado);

        mockMvc.perform(post("/pagamento/pedido/100/boleto").session(mockSession))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/pagamento/pedido/100/boleto/ver"))
            .andExpect(flash().attributeExists("successMessage", "codigoBarrasBoleto", "urlBoleto", "dataVencimentoBoleto"));
    }

    @Test
    void testVerBoleto_Success() throws Exception {
        PagamentoBoleto boleto = new PagamentoBoleto();
        boleto.setCodigoBarras("BOLETO_CODE_123");
        boleto.setUrlBoleto("http://example.com/boleto/123");
        boleto.setDataVencimento(new Date());
        boleto.setStatus(StatusPagamento.PENDENTE); // Important: ensure it's a pending boleto

        pedido.setPagamento(boleto); // Link boleto to order

        when(pedidoRepository.findById(100)).thenReturn(Optional.of(pedido));

        mockMvc.perform(get("/pagamento/pedido/100/boleto/ver").session(mockSession))
            .andExpect(status().isOk())
            .andExpect(view().name("checkout/pagamento-boleto"))
            .andExpect(model().attributeExists("pedido", "boleto"));
    }

    // Test for simularConfirmacaoPagamento (webhook simulation)
    @Test
    void testSimularConfirmacaoPagamento() throws Exception {
        Pagamento pagamentoConfirmado = new PagamentoCartao(); // Or PagamentoBoleto
        pagamentoConfirmado.setId(300);
        pagamentoConfirmado.setPedido(pedido); // Link back
        pagamentoConfirmado.setStatus(StatusPagamento.APROVADO);
        pedido.setStatus(StatusPedido.PROCESSANDO);


        when(pagamentoService.confirmarPagamento(300)).thenReturn(pagamentoConfirmado);

        mockMvc.perform(get("/pagamento/simular-webhook/confirmar/300"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Pagamento ID 300 confirmado")));
    }

    // Test for simularFalhaPagamento (webhook simulation)
    @Test
    void testSimularFalhaPagamento() throws Exception {
        Pagamento pagamentoFalha = new PagamentoCartao(); // Or PagamentoBoleto
        pagamentoFalha.setId(301);
        pagamentoFalha.setPedido(pedido); // Link back
        pagamentoFalha.setStatus(StatusPagamento.RECUSADO);
        pedido.setStatus(StatusPedido.CANCELADO);

        when(pagamentoService.cancelarOuFalharPagamento(301)).thenReturn(pagamentoFalha);

        mockMvc.perform(get("/pagamento/simular-webhook/falhar/301"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("Pagamento ID 301 falhou")));
    }
}
