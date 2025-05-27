package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Pedido;
import com.trabalho.cliqaqui.model.User; // Importe a classe User
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // Importe List

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Adicione este método para buscar pedidos por cliente
    List<Pedido> findByCliente(User cliente);
}