package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Produto; // Renomeado de CliqModel para Produto
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // Método para buscar produtos por nome
    List<Produto> findByNomeContainingIgnoreCase(String nome);

    // Método para buscar produtos por categoria
    @Query("SELECT p FROM Produto p JOIN p.categorias c WHERE c.nome = :nomeCategoria")
    List<Produto> findByCategoriaNome(@Param("nomeCategoria") String nomeCategoria);
}