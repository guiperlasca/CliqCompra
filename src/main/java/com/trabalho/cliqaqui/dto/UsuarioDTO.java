package com.trabalho.cliqaqui.dto;

public class UsuarioDTO {
    private String nome;
    private String email;
    private String senha; // Plain text password from form
    private String cpfCnpj;

    public UsuarioDTO() {
    }

    // Getters and Setters for all fields
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }
}
