package com.trabalho.cliqaqui.dto;

import com.trabalho.cliqaqui.model.Produto;
import java.util.List;
import java.util.stream.Collectors;

public class ProdutoDTO {
    private Long id;
    private String nome;
    private String descricao;
    private Double preco;
    private List<Long> categoriasIds; // Para receber IDs de categorias no cadastro/edição
    private List<String> categoriasNomes; // Para retornar nomes das categorias

    public ProdutoDTO() {
    }

    public ProdutoDTO(Produto produto) {
        this.id = produto.getId();
        this.nome = produto.getNome();
        this.descricao = produto.getDescricao();
        this.preco = produto.getPreco();
        this.categoriasNomes = produto.getCategorias().stream()
                .map(categoria -> categoria.getNome())
                .collect(Collectors.toList());
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public List<Long> getCategoriasIds() {
        return categoriasIds;
    }

    public void setCategoriasIds(List<Long> categoriasIds) {
        this.categoriasIds = categoriasIds;
    }

    public List<String> getCategoriasNomes() {
        return categoriasNomes;
    }

    public void setCategoriasNomes(List<String> categoriasNomes) {
        this.categoriasNomes = categoriasNomes;
    }
}