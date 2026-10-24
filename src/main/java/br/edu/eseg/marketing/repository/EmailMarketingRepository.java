package br.edu.eseg.marketing.repository;

import br.edu.eseg.marketing.domain.EmailMarketing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EmailMarketingRepository extends JpaRepository<EmailMarketing, Long> {

    @Query("SELECT e FROM EmailMarketing e JOIN FETCH e.conta ORDER BY e.data DESC")
    List<EmailMarketing> listarTodosComConta();

    List<EmailMarketing> findByContaIdConta(Long idConta);
}
