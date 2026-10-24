package br.edu.eseg.marketing.repository;

import br.edu.eseg.marketing.domain.DesempenhoDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface DesempenhoDiarioRepository extends JpaRepository<DesempenhoDiario, Long> {

    @Query("SELECT d FROM DesempenhoDiario d " +
            "JOIN FETCH d.anuncio a " +
            "JOIN FETCH a.campanha c " +
            "JOIN FETCH c.conta " +
            "JOIN FETCH d.publicoAlvo " +
            "ORDER BY d.data DESC")
    List<DesempenhoDiario> listarTodosCompletos();

    @Query("SELECT COALESCE(SUM(d.custo), 0) FROM DesempenhoDiario d WHERE d.anuncio.campanha.idCampanha = :idCampanha")
    BigDecimal somarCustoDiarioPorCampanha(@Param("idCampanha") Long idCampanha);
}
