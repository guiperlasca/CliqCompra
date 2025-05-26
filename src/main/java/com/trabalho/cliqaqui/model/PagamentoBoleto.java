package com.trabalho.cliqaqui.model;

import java.util.Date;

public class PagamentoBoleto extends Pagamento {
    private String codigoBarras;
    private Date dataVencimento;

    public void emitirBoleto() {
        // Placeholder
    }

    @Override
    public boolean processarPagamento() {
        // Placeholder
        return false;
    }
}
