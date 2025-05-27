package com.trabalho.cliqaqui.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "pagamentos_boleto")
public class PagamentoBoleto extends Pagamento {

    private LocalDate dataVencimento;
    private String codigoBarra;

    public PagamentoBoleto() {
    }

    // Getters e Setters
    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(LocalDate dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }
}