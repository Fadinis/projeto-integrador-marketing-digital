package br.edu.eseg.marketing.repository;

import br.edu.eseg.marketing.domain.LandingPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface LandingPageRepository extends JpaRepository<LandingPage, Long> {

    @Query("SELECT lp FROM LandingPage lp JOIN FETCH lp.campanha ORDER BY lp.idLandingPage DESC")
    List<LandingPage> listarTodasComCampanha();

    List<LandingPage> findByCampanhaIdCampanha(Long idCampanha);
}
