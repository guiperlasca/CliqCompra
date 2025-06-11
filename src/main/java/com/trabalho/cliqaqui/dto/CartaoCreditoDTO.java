package com.trabalho.cliqaqui.dto;

// Consider adding validation annotations later (e.g., javax.validation.constraints or jakarta.validation.constraints)
// For example: @NotBlank, @Size, @Pattern, etc.

public class CartaoCreditoDTO {

    private String numeroCartao; // In real PCI-compliant app, this would likely be a token from gateway
    private String nomeTitular;
    private String mesValidade; // e.g., "01"-"12"
    private String anoValidade; // e.g., "2023"
    private String cvv;         // In real PCI-compliant app, this would be handled by gateway's secure input
    private int numeroParcelas;
    // Optional: bandeiraCartao if client can determine it, otherwise server-side or gateway.

    public CartaoCreditoDTO() {
    }

    // Getters and Setters

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public String getNomeTitular() {
        return nomeTitular;
    }

    public void setNomeTitular(String nomeTitular) {
        this.nomeTitular = nomeTitular;
    }

    public String getMesValidade() {
        return mesValidade;
    }

    public void setMesValidade(String mesValidade) {
        this.mesValidade = mesValidade;
    }

    public String getAnoValidade() {
        return anoValidade;
    }

    public void setAnoValidade(String anoValidade) {
        this.anoValidade = anoValidade;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public int getNumeroParcelas() {
        return numeroParcelas;
    }

    public void setNumeroParcelas(int numeroParcelas) {
        this.numeroParcelas = numeroParcelas;
    }
}
