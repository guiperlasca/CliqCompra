package com.trabalho.cliqaqui.model;

import jakarta.persistence.*;

@Entity
@Table(name = "itens_pedido")
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto; // Renomeado de CliqModel para Produto

    private int quantidade;
    private Double precoUnitarioMomentoCompra;
    private Double descontoAplicado;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    public ItemPedido() {
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoUnitarioMomentoCompra() {
        return precoUnitarioMomentoCompra;
    }

    public void setPrecoUnitarioMomentoCompra(Double precoUnitarioMomentoCompra) {
        this.precoUnitarioMomentoCompra = precoUnitarioMomentoCompra;
    }

    public Double getDescontoAplicado() {
        return descontoAplicado;
    }

    public void setDescontoAplicado(Double descontoAplicado) {
        this.descontoAplicado = descontoAplicado;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }
}