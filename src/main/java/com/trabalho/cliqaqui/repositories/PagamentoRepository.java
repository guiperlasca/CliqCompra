package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Integer> {
    // Custom query methods can be added here later if needed
}
