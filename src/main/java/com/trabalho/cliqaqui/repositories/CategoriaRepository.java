package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    // Custom query methods can be added here later if needed
}
