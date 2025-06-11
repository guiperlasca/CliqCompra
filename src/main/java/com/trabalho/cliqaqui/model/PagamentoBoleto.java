package com.trabalho.cliqaqui.model;

import jakarta.persistence.Entity;
import java.util.Date;

@Entity
public class PagamentoBoleto extends Pagamento {
    private String codigoBarras;
    private Date dataVencimento;
    private String urlBoleto;

    public PagamentoBoleto() {
        super();
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getUrlBoleto() {
        return urlBoleto;
    }

    public void setUrlBoleto(String urlBoleto) {
        this.urlBoleto = urlBoleto;
    }

    public void emitirBoleto() {
        // Placeholder
    }

    @Override
    public boolean processarPagamento() {
        // Placeholder
        return false;
    }
}
