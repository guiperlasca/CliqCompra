package com.trabalho.cliqaqui.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "pagamentos_cartao")
public class PagamentoCartao extends Pagamento {

    private String numeroCartao; // Considerar criptografia ou mascaramento
    private String bandeira;
    private Integer numeroParcelas;

    public PagamentoCartao() {
    }

    // Getters e Setters
    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public String getBandeira() {
        return bandeira;
    }

    public void setBandeira(String bandeira) {
        this.bandeira = bandeira;
    }

    public Integer getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(Integer numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }
}