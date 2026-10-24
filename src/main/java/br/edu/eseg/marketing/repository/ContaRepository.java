package br.edu.eseg.marketing.repository;

import br.edu.eseg.marketing.domain.Conta;
import br.edu.eseg.marketing.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    List<Conta> findByStatusConta(Status statusConta);
}