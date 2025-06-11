package com.trabalho.cliqaqui.dto;

import com.trabalho.cliqaqui.model.StatusPagamento;

public class PagamentoResponseDTO {

    private boolean sucesso;
    private String mensagem; // e.g., "Pagamento aprovado", "Boleto gerado com sucesso", "Falha no pagamento: Saldo insuficiente"
    private StatusPagamento statusPagamento;
    private Integer pedidoId;
    private String tipoPagamento; // "CARTAO", "BOLETO"

    // Boleto specific fields (optional, only populated if boleto)
    private String urlBoleto;
    private String codigoBarrasBoleto;
    private String dataVencimentoBoleto;

    // Card specific fields (optional, only populated if card)
    private String bandeiraCartao;
    private String ultimosQuatroDigitosCartao;
    private Integer numeroParcelasCartao;


    public PagamentoResponseDTO() {
    }

    // Constructor for general responses
    public PagamentoResponseDTO(boolean sucesso, String mensagem, StatusPagamento statusPagamento, Integer pedidoId, String tipoPagamento) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.statusPagamento = statusPagamento;
        this.pedidoId = pedidoId;
        this.tipoPagamento = tipoPagamento;
    }


    // Getters and Setters

    public boolean isSucesso() {
        return sucesso;
    }

    public void setSucesso(boolean sucesso) {
        this.sucesso = sucesso;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public Integer getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Integer pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(String tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public String getUrlBoleto() {
        return urlBoleto;
    }

    public void setUrlBoleto(String urlBoleto) {
        this.urlBoleto = urlBoleto;
    }

    public String getCodigoBarrasBoleto() {
        return codigoBarrasBoleto;
    }

    public void setCodigoBarrasBoleto(String codigoBarrasBoleto) {
        this.codigoBarrasBoleto = codigoBarrasBoleto;
    }

    public String getDataVencimentoBoleto() {
        return dataVencimentoBoleto;
    }

    public void setDataVencimentoBoleto(String dataVencimentoBoleto) {
        this.dataVencimentoBoleto = dataVencimentoBoleto;
    }

    public String getBandeiraCartao() {
        return bandeiraCartao;
    }

    public void setBandeiraCartao(String bandeiraCartao) {
        this.bandeiraCartao = bandeiraCartao;
    }

    public String getUltimosQuatroDigitosCartao() {
        return ultimosQuatroDigitosCartao;
    }

    public void setUltimosQuatroDigitosCartao(String ultimosQuatroDigitosCartao) {
        this.ultimosQuatroDigitosCartao = ultimosQuatroDigitosCartao;
    }

    public Integer getNumeroParcelasCartao() {
        return numeroParcelasCartao;
    }

    public void setNumeroParcelasCartao(Integer numeroParcelasCartao) {
        this.numeroParcelasCartao = numeroParcelasCartao;
    }
}
