package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    Optional<Categoria> findByNomeIgnoreCase(String nome);

    List<Categoria> findAllByOrderByNomeAsc();

    // For Phase 2, Step 7 (processAddProduct), if needing to fetch multiple categories by ID:
    // List<Categoria> findAllByIdIn(List<Integer> ids);
    // This can be added later if findAllById (from JpaRepository) isn't sufficient or if specific ordering is needed.
    // JpaRepository already provides List<T> findAllById(Iterable<ID> ids);
}
