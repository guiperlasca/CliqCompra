package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.ItemCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCarrinhoRepository extends JpaRepository<ItemCarrinho, Integer> {
    // Custom query methods can be added here if needed later
}
