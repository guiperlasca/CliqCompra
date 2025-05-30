package com.trabalho.cliqaqui.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Vendedor extends Usuario {

    @OneToMany(mappedBy = "vendedor", cascade = CascadeType.ALL)
    private List<Produto> produtos = new ArrayList<>();

    // A Vendedor might not directly "own" Pedidos in the same way a Cliente does.
    // The visualizarPedidos() method might involve querying Pedidos based on the Vendedor's produtos.
    // For now, no direct JPA relationship for Pedidos from Vendedor based on the provided UML interpretation.

    public Vendedor() {
        super();
    }

    public List<Produto> getProdutos() {
        return produtos;
    }

    public void setProdutos(List<Produto> produtos) {
        this.produtos = produtos;
    }

    public void cadastrar() {
        // Placeholder
    }

    public void cadastrarProduto(Produto produto) {
        // Placeholder: Actual logic would involve adding to this.produtos and persisting.
        // this.produtos.add(produto);
        // produto.setVendedor(this); // If bidirectional
    }

    public void aplicarDescontoItem(ItemPedido item, double valorDesconto) {
        // Placeholder
    }

    public List<Pedido> visualizarPedidos() {
        // Placeholder: Actual implementation would query Pedidos based on this Vendedor's produtos.
        return new ArrayList<>(); // Return empty list for now
    }
}
