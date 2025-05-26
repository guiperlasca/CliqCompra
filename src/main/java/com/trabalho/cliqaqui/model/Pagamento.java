package com.trabalho.cliqaqui.model;

import java.sql.Timestamp;

public abstract class Pagamento {
    private Integer id;
    private double valor;
    private Timestamp dataPagamento;
    private StatusPagamento status;

    public abstract boolean processarPagamento();
}
