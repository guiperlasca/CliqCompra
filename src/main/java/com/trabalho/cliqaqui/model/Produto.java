package com.trabalho.cliqaqui.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private double preco;
    private String descricao;
    private String fotoUrl;

    @ManyToMany
    @JoinTable(
            name = "produto_categoria",
            joinColumns = @JoinColumn(name = "produto_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id")
    )
    private List<Categoria> categorias = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "usuario_id") // Changed column name
    private Usuario usuario; // Changed type

    public Produto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public List<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(List<Categoria> categorias) {
        this.categorias = categorias;
    }

    public Usuario getUsuario() { // Renamed method, changed return type
        return usuario;
    }

    public void setUsuario(Usuario usuario) { // Renamed method, changed parameter type
        this.usuario = usuario;
    }

    public void addCategoria(Categoria categoria) {
        if (this.categorias == null) {
            this.categorias = new ArrayList<>();
        }
        if (!this.categorias.contains(categoria)) {
            this.categorias.add(categoria);
            if (categoria.getProdutos() == null) { // Ensure other side's list is initialized
                categoria.setProdutos(new ArrayList<>());
            }
            if (!categoria.getProdutos().contains(this)) {
                categoria.getProdutos().add(this);
            }
        }
    }

    public void removeCategoria(Categoria categoria) {
        if (this.categorias != null && this.categorias.contains(categoria)) {
            this.categorias.remove(categoria);
            if (categoria.getProdutos() != null && categoria.getProdutos().contains(this)) {
                categoria.getProdutos().remove(this);
            }
        }
    }
}
