package br.edu.eseg.marketing.repository;

import br.edu.eseg.marketing.domain.ResumoFunil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ResumoFunilRepository extends JpaRepository<ResumoFunil, Long> {

    @Query("SELECT r FROM ResumoFunil r JOIN FETCH r.campanha WHERE r.campanha.idCampanha = :idCampanha ORDER BY r.data DESC")
    List<ResumoFunil> listarPorCampanha(@Param("idCampanha") Long idCampanha);

    @Query("SELECT COALESCE(SUM(r.custo), 0) FROM ResumoFunil r WHERE r.campanha.idCampanha = :idCampanha")
    BigDecimal somarCustoPorCampanha(@Param("idCampanha") Long idCampanha);
}
