package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Vendedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendedorRepository extends JpaRepository<Vendedor, Integer> {
    // Custom query methods can be added here later if needed
}
