package com.trabalho.cliqaqui.model;

public class PagamentoCartao extends Pagamento {
    private String numeroCartaoToken;
    private String nomeTitular;
    private String dataValidade;
    private int numeroParcelas;

    public void registrarPagamentoCartao() {
        // Placeholder
    }

    @Override
    public boolean processarPagamento() {
        // Placeholder
        return false;
    }
}
