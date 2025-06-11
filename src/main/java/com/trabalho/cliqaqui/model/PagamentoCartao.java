package com.trabalho.cliqaqui.model;

import jakarta.persistence.Entity;

@Entity
public class PagamentoCartao extends Pagamento {
    private String numeroCartaoToken;
    private String nomeTitular;
    private String dataValidade;
    private int numeroParcelas;
    private String bandeiraCartao;
    private String ultimosQuatroDigitos;

    public PagamentoCartao() {
        super();
    }

    public String getNumeroCartaoToken() {
        return numeroCartaoToken;
    }

    public void setNumeroCartaoToken(String numeroCartaoToken) {
        this.numeroCartaoToken = numeroCartaoToken;
    }

    public String getNomeTitular() {
        return nomeTitular;
    }

    public void setNomeTitular(String nomeTitular) {
        this.nomeTitular = nomeTitular;
    }

    public String getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(String dataValidade) {
        this.dataValidade = dataValidade;
    }

    public int getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(int numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }

    public String getBandeiraCartao() {
        return bandeiraCartao;
    }

    public void setBandeiraCartao(String bandeiraCartao) {
        this.bandeiraCartao = bandeiraCartao;
    }

    public String getUltimosQuatroDigitos() {
        return ultimosQuatroDigitos;
    }

    public void setUltimosQuatroDigitos(String ultimosQuatroDigitos) {
        this.ultimosQuatroDigitos = ultimosQuatroDigitos;
    }

    public void registrarPagamentoCartao() {
        // Placeholder
    }

    @Override
    public boolean processarPagamento() {
        // Placeholder
        return false;
    }
}
