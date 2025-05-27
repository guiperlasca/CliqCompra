package com.trabalho.cliqaqui.model;

public enum StatusPedido {
    PENDENTE,    // Pedido criado, aguardando pagamento
    PROCESSANDO, // Pagamento confirmado, pedido em preparação
    ENVIADO,     // Pedido enviado para o cliente
    ENTREGUE,    // Pedido entregue
    CANCELADO    // Pedido cancelado
}