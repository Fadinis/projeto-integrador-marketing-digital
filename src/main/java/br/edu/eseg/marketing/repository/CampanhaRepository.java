package br.edu.eseg.marketing.repository;

import br.edu.eseg.marketing.domain.Campanha;
import br.edu.eseg.marketing.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CampanhaRepository extends JpaRepository<Campanha, Long> {

    @Query("SELECT c FROM Campanha c JOIN FETCH c.conta ORDER BY c.idCampanha DESC")
    List<Campanha> listarTodasComConta();

    @Query("SELECT c FROM Campanha c JOIN FETCH c.conta WHERE c.idCampanha = :id")
    Optional<Campanha> buscarPorIdComConta(@Param("id") Long id);

    List<Campanha> findByStatusCampanha(Status statusCampanha);

    List<Campanha> findByContaIdConta(Long idConta);
}
