package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Integer> {
    // Custom query methods can be added here later if needed
}
