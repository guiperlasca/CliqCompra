package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Carrinho;
import com.trabalho.cliqaqui.model.Usuario; // Import Usuario
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Integer> {
    Optional<Carrinho> findByUsuario(Usuario usuario);
    // Or, if you prefer to query by ID directly:
    // Optional<Carrinho> findByUsuarioId(Integer usuarioId);
}
