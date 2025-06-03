package com.trabalho.cliqaqui.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cliente extends Usuario {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cliente_id") // Assumes Endereco does not have a direct reference back to Cliente
    private List<Endereco> enderecos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Pedido> pedidos = new ArrayList<>();

    public Cliente() {
        super();
    }

    public List<Endereco> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<Endereco> enderecos) {
        this.enderecos = enderecos;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }

    public void cadastrar() {
        // Placeholder
    }

    public void realizarPedido(Pedido pedido) {
        // Placeholder
    }

    public void selecionarEnderecoEntrega(Endereco endereco) {
        // Placeholder
    }

    public List<Pedido> visualizarPedidos() {
        // Placeholder, actual implementation would use this.pedidos
        return this.pedidos;
    }

    public void adicionarProdutoAoCarrinho(Produto produto, int quantidade) {
        // Placeholder
    }
}
