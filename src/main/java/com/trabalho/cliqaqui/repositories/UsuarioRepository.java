package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    // Custom query methods can be added here later if needed
}
