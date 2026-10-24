package br.edu.eseg.marketing.repository;

import br.edu.eseg.marketing.domain.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {

    @Query("SELECT a FROM Anuncio a JOIN FETCH a.campanha c JOIN FETCH c.conta ORDER BY a.idAnuncio DESC")
    List<Anuncio> listarTodosComCampanha();

    @Query("SELECT a FROM Anuncio a JOIN FETCH a.campanha c JOIN FETCH c.conta WHERE a.idAnuncio = :id")
    Optional<Anuncio> buscarPorIdComCampanha(@Param("id") Long id);

    List<Anuncio> findByCampanhaIdCampanha(Long idCampanha);
}
