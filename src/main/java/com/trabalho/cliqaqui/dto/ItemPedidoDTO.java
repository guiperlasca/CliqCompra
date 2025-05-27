package com.trabalho.cliqaqui.dto;

public class ItemPedidoDTO {
    private Long produtoId;
    private int quantidade;
    private double descontoAplicado;

    public double getDescontoAplicado() {
        return descontoAplicado;
    }

    public void setDescontoAplicado(double descontoAplicado) {
        this.descontoAplicado = descontoAplicado;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }
}