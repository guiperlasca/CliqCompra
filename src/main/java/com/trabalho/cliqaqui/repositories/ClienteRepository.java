package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    // Custom query methods can be added here later if needed
}
