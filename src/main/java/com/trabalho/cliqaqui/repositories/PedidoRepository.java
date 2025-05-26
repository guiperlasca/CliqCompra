package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    // Custom query methods can be added here later if needed
}
