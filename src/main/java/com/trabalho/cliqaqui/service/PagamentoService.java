package com.trabalho.cliqaqui.service;

import com.trabalho.cliqaqui.dto.CartaoCreditoDTO;
import com.trabalho.cliqaqui.model.Pagamento;
import com.trabalho.cliqaqui.model.PagamentoCartao;
import com.trabalho.cliqaqui.model.PagamentoBoleto; // Added for future use
import com.trabalho.cliqaqui.model.Pedido;
import com.trabalho.cliqaqui.model.StatusPagamento;
import com.trabalho.cliqaqui.model.StatusPedido;
import com.trabalho.cliqaqui.repositories.PagamentoRepository;
import com.trabalho.cliqaqui.repositories.PedidoRepository; // To fetch/update Pedido

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date; // For Boleto
import java.util.Optional;
import java.util.UUID; // For simulating tokens or transaction IDs

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository; // Using PedidoRepository directly for now
                                                   // Alternatively, could use PedidoService

    @Autowired
    public PagamentoService(PagamentoRepository pagamentoRepository, PedidoRepository pedidoRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public PagamentoCartao processarPagamentoCartao(Integer pedidoId, CartaoCreditoDTO cartaoDto) {
        Optional<Pedido> optionalPedido = pedidoRepository.findById(pedidoId);
        if (!optionalPedido.isPresent()) {
            throw new RuntimeException("Pedido não encontrado: " + pedidoId); // Or a custom exception
        }
        Pedido pedido = optionalPedido.get();

        // Simulate Gateway Interaction (Authorization and Capture)
        // In a real scenario, you'd call the payment gateway SDK here.
        // The gateway would return a transaction ID, status, etc.
        boolean pagamentoAprovadoPeloGateway = simularAutorizacaoCartao(cartaoDto, pedido.getValorTotal());

        PagamentoCartao pagamento = new PagamentoCartao();
        pagamento.setPedido(pedido);
        pagamento.setValor(pedido.getValorTotal());
        pagamento.setDataPagamento(Timestamp.from(Instant.now())); // Or data de criação

        // Simulate tokenization or storing gateway reference
        pagamento.setNumeroCartaoToken("TOKEN_" + UUID.randomUUID().toString());
        pagamento.setNomeTitular(cartaoDto.getNomeTitular());
        // Combine month and year for storage if your model expects "MM/YYYY" or similar
        pagamento.setDataValidade(cartaoDto.getMesValidade() + "/" + cartaoDto.getAnoValidade());
        pagamento.setNumeroParcelas(cartaoDto.getNumeroParcelas());

        // Simulate getting card brand and last 4 digits (a real gateway might provide this)
        pagamento.setBandeiraCartao(simularDeteccaoBandeira(cartaoDto.getNumeroCartao()));
        pagamento.setUltimosQuatroDigitos(extrairUltimosQuatroDigitos(cartaoDto.getNumeroCartao()));

        if (pagamentoAprovadoPeloGateway) {
            pagamento.setStatus(StatusPagamento.APROVADO);
            pedido.setStatus(StatusPedido.PAGO); // Or PROCESSANDO_PAGAMENTO then PAGO via webhook
        } else {
            pagamento.setStatus(StatusPagamento.RECUSADO);
            pedido.setStatus(StatusPedido.FALHA_PAGAMENTO);
        }

        PagamentoCartao pagamentoSalvo = pagamentoRepository.save(pagamento);
        pedido.setPagamento(pagamentoSalvo);
        pedidoRepository.save(pedido); // Save pedido to update its status and link to pagamento

        return pagamentoSalvo;
    }

    // --- Simulation Helper Methods ---
    private boolean simularAutorizacaoCartao(CartaoCreditoDTO cartaoDto, double valor) {
        // Basic simulation:
        // - Decline if CVV is "000" or card number ends in "1111" (configurable for testing)
        // - Decline if amount is > 10000 (arbitrary limit)
        // - Otherwise, approve.
        System.out.println("Simulando autorização para cartão: " + cartaoDto.getNumeroCartao() + " no valor de " + valor);
        if ("000".equals(cartaoDto.getCvv()) || (cartaoDto.getNumeroCartao() != null && cartaoDto.getNumeroCartao().endsWith("1111"))) {
            System.out.println("Simulação: Pagamento RECUSADO (CVV ou número de cartão de teste para recusa)");
            return false;
        }
        if (valor > 10000) {
            System.out.println("Simulação: Pagamento RECUSADO (valor muito alto)");
            return false;
        }
        System.out.println("Simulação: Pagamento APROVADO");
        return true;
    }

    private String simularDeteccaoBandeira(String numeroCartao) {
        if (numeroCartao == null || numeroCartao.isEmpty()) {
            return "DESCONHECIDA";
        }
        // Basic simulation based on first digit (very simplified)
        if (numeroCartao.startsWith("4")) return "VISA";
        if (numeroCartao.startsWith("5")) return "MASTERCARD";
        if (numeroCartao.startsWith("3")) return "AMEX";
        return "OUTRA";
    }

    private String extrairUltimosQuatroDigitos(String numeroCartao) {
        if (numeroCartao != null && numeroCartao.length() > 4) {
            return numeroCartao.substring(numeroCartao.length() - 4);
        }
        return "XXXX"; // Or null if preferred
    }

    @Transactional
    public PagamentoBoleto processarPagamentoBoleto(Integer pedidoId) {
        Optional<Pedido> optionalPedido = pedidoRepository.findById(pedidoId);
        if (!optionalPedido.isPresent()) {
            throw new RuntimeException("Pedido não encontrado: " + pedidoId); // Or a custom exception
        }
        Pedido pedido = optionalPedido.get();

        // Simulate Gateway Interaction to generate Boleto
        String codigoBarrasSimulado = UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase(); // Simpler for example
        String urlBoletoSimulado = "https://simulado.gateway.com/boleto/" + pedido.getId() + "/" + UUID.randomUUID().toString();
        long timeNow = System.currentTimeMillis();
        long sevenDaysInMillis = 7 * 24 * 60 * 60 * 1000L;
        Date dataVencimentoSimulada = new Date(timeNow + sevenDaysInMillis);


        PagamentoBoleto pagamento = new PagamentoBoleto();
        pagamento.setPedido(pedido);
        pagamento.setValor(pedido.getValorTotal());
        pagamento.setDataPagamento(new Timestamp(timeNow)); // Data de emissão do boleto
        pagamento.setStatus(StatusPagamento.PENDENTE); // Boleto starts as pending

        pagamento.setCodigoBarras(codigoBarrasSimulado);
        pagamento.setUrlBoleto(urlBoletoSimulado);
        pagamento.setDataVencimento(dataVencimentoSimulada);

        PagamentoBoleto pagamentoSalvo = pagamentoRepository.save(pagamento);

        pedido.setPagamento(pagamentoSalvo);
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO); // Update order status
        pedidoRepository.save(pedido);

        System.out.println("Simulação: Boleto gerado para pedido " + pedidoId + " com código de barras: " + codigoBarrasSimulado);
        return pagamentoSalvo;
    }

    @Transactional
    public Pagamento confirmarPagamento(Integer pagamentoId) {
        Optional<Pagamento> optionalPagamento = pagamentoRepository.findById(pagamentoId);
        if (!optionalPagamento.isPresent()) {
            throw new RuntimeException("Pagamento não encontrado: " + pagamentoId);
        }
        Pagamento pagamento = optionalPagamento.get();
        Pedido pedido = pagamento.getPedido();

        if (pedido == null) {
            throw new RuntimeException("Pedido associado ao pagamento não encontrado para o pagamento ID: " + pagamentoId);
        }

        // Simulate successful payment confirmation
        pagamento.setStatus(StatusPagamento.APROVADO);
        // Assuming dataPagamento was date of creation/attempt, let's add a confirmation timestamp if needed
        // For now, the existing dataPagamento can serve as confirmation time for simplicity in this simulation
        // pagamento.setDataConfirmacao(Timestamp.from(Instant.now()));

        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);

        pedido.setStatus(StatusPedido.PAGO);
        pedidoRepository.save(pedido);

        System.out.println("Simulação: Pagamento ID " + pagamentoId + " confirmado para Pedido ID " + pedido.getId());
        return pagamentoSalvo;
    }

    @Transactional
    public Pagamento cancelarOuFalharPagamento(Integer pagamentoId) {
        Optional<Pagamento> optionalPagamento = pagamentoRepository.findById(pagamentoId);
        if (!optionalPagamento.isPresent()) {
            throw new RuntimeException("Pagamento não encontrado: " + pagamentoId);
        }
        Pagamento pagamento = optionalPagamento.get();
        Pedido pedido = pagamento.getPedido();

        if (pedido == null) {
            throw new RuntimeException("Pedido associado ao pagamento não encontrado para o pagamento ID: " + pagamentoId);
        }

        // Simulate payment failure or cancellation
        pagamento.setStatus(StatusPagamento.RECUSADO); // Or a new status like CANCELADO
        Pagamento pagamentoSalvo = pagamentoRepository.save(pagamento);

        pedido.setStatus(StatusPedido.FALHA_PAGAMENTO); // Or CANCELADO if applicable
        pedidoRepository.save(pedido);

        System.out.println("Simulação: Pagamento ID " + pagamentoId + " marcado como RECUSADO/FALHA para Pedido ID " + pedido.getId());
        return pagamentoSalvo;
    }
}
