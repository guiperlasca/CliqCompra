package com.trabalho.cliqaqui.dto;

import com.trabalho.cliqaqui.model.Produto; // Renomeado de CliqModel para Produto

public class ProdutoMinDTO {

    private Long id;
    private String nome;

    public ProdutoMinDTO(Produto entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
    }

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
}