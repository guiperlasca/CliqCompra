package com.trabalho.cliqaqui.model;

import jakarta.persistence.*;

@Entity
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int quantidade;
    private double precoUnitarioMomentoCompra;
    private double subtotal;
    private double descontoAplicado;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    public ItemPedido() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPrecoUnitarioMomentoCompra() {
        return precoUnitarioMomentoCompra;
    }

    public void setPrecoUnitarioMomentoCompra(double precoUnitarioMomentoCompra) {
        this.precoUnitarioMomentoCompra = precoUnitarioMomentoCompra;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDescontoAplicado() {
        return descontoAplicado;
    }

    public void setDescontoAplicado(double descontoAplicado) {
        this.descontoAplicado = descontoAplicado;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
}
