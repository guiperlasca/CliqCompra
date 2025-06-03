package com.trabalho.cliqaqui.dto;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.ArrayList;

public class ProdutoDTO {
    private String nome;
    private double preco;
    private String descricao;
    private MultipartFile fotoFile;
    private List<Integer> categoriaIds = new ArrayList<>(); // Initialize to prevent NPE
    // Categories can be added later if needed.

    public ProdutoDTO() {
    }

    // Getters and Setters for all fields
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

    public MultipartFile getFotoFile() {
        return fotoFile;
    }

    public void setFotoFile(MultipartFile fotoFile) {
        this.fotoFile = fotoFile;
    }

    public List<Integer> getCategoriaIds() {
        return categoriaIds;
    }

    public void setCategoriaIds(List<Integer> categoriaIds) {
        this.categoriaIds = categoriaIds;
    }
}
