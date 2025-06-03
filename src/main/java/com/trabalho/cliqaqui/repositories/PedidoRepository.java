package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Ensure this import is present
// import com.trabalho.cliqaqui.model.Pedido; // Ensure this import is present

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByClienteIdOrderByDataRealizacaoDesc(Integer clienteId);
    // Custom query methods can be added here later if needed
}
