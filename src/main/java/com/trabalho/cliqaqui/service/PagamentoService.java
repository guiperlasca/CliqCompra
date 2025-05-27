package com.trabalho.cliqaqui.service;

import com.trabalho.cliqaqui.model.*;
import com.trabalho.cliqaqui.repositories.PagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PagamentoService {

    @Autowired
    private PagamentoRepository pagamentoRepo;

    public Pagamento processarPagamento(Pedido pedido, String formaPagamento, Integer numeroParcelas) {
        Pagamento pagamento;

        if ("BOLETO".equalsIgnoreCase(formaPagamento)) { // Permite pagamento por boleto [cite: 26]
            PagamentoBoleto boleto = new PagamentoBoleto();
            boleto.setDataVencimento(LocalDate.now().plusDays(5)); // Vencimento em 5 dias [cite: 28]
            boleto.setCodigoBarra(gerarCodigoBarra()); // Simula um código de barras
            pagamento = boleto;
        } else if ("CARTAO".equalsIgnoreCase(formaPagamento)) { // Permite pagamento por cartão de crédito [cite: 26]
            PagamentoCartao cartao = new PagamentoCartao();
            cartao.setBandeira("VISA"); // Exemplo
            cartao.setNumeroCartao("XXXX.XXXX.XXXX.1234"); // Exemplo mascarado
            cartao.setNumeroParcelas(numeroParcelas != null ? numeroParcelas : 1); // Permite parcelamento [cite: 29]
            pagamento = cartao;
        } else {
            throw new IllegalArgumentException("Forma de pagamento inválida: " + formaPagamento);
        }

        pagamento.setValor(pedido.getValorTotal());
        pagamento.setDataPagamento(LocalDateTime.now());
        pagamento.setStatus(StatusPagamento.QUITADO); // Simula pagamento sempre quitado para simplificar [cite: 31]

        pagamentoRepo.save(pagamento);
        return pagamento;
    }

    private String gerarCodigoBarra() {
        // Simulação de geração de código de barras
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 44; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}