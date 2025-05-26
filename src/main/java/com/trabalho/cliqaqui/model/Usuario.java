package com.trabalho.cliqaqui.model;

import java.util.List;

public abstract class Usuario {
    private Integer id;
    private String nome;
    private String email;
    protected String senhaHash;
    private String cpfCnpj;
    private List<String> telefones;

    public boolean fazerLogin(String email, String senha) {
        // Placeholder
        return false;
    }

    public void editarPerfil() {
        // Placeholder
    }
}
