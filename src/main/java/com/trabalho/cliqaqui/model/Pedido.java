package com.trabalho.cliqaqui.model;

import java.sql.Timestamp;
import java.util.List;

public class Pedido {
    private Integer id;
    private Timestamp dataRealizacao;
    private StatusPedido status;
    private double valorTotal;
    private List<ItemPedido> itens;
    private Cliente cliente;
    private Endereco enderecoEntrega;
    private Pagamento pagamento;

    public void adicionarItem(Produto produto, int quantidade) {
        // Placeholder
    }

    public void removerItem(Produto produto) {
        // Placeholder
    }

    public double calcularTotal() {
        // Placeholder
        return 0.0;
    }

    public void finalizarPedido() {
        // Placeholder
    }

    public void atualizarStatus(StatusPedido novoStatus) {
        // Placeholder
    }

    public void selecionarFormaPagamento(Pagamento pagamento) {
        // Placeholder
    }
}
