package com.trabalho.cliqaqui.repositories;

import com.trabalho.cliqaqui.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
}