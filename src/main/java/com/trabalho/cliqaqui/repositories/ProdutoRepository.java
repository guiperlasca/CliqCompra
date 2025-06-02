package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List; // Ensure this import is present

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    List<Produto> findByUsuarioId(Integer usuarioId);
    // Custom query methods can be added here later if needed
}
