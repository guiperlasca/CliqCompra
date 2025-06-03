package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnderecoRepository extends JpaRepository<Endereco, Integer> {
    // Custom query methods can be added here later if needed
}
