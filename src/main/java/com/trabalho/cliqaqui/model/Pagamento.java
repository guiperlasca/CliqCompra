package com.trabalho.cliqaqui.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private double valor;
    private Timestamp dataPagamento;

    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    @OneToOne(mappedBy = "pagamento")
    private Pedido pedido;

    public Pagamento() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Timestamp getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Timestamp dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public void setStatus(StatusPagamento status) {
        this.status = status;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public abstract boolean processarPagamento();
}
