package br.edu.eseg.marketing;

import br.edu.eseg.marketing.domain.Campanha;
import br.edu.eseg.marketing.domain.Status;
import br.edu.eseg.marketing.exception.RegraNegocioException;
import br.edu.eseg.marketing.repository.*;
import br.edu.eseg.marketing.service.CampanhaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CampanhaServiceTest {

    @Autowired
    private CampanhaService campanhaService;

    @Autowired
    private CampanhaRepository campanhaRepository;

    @Test
    @DisplayName("Deve impedir exclusão de campanha com status ATIVO")
    void deveImpedirExclusaoCampanhaAtiva() {
        // Busca a primeira campanha ATIVA inserida pelo DataInitializer
        Campanha campanhaAtiva = campanhaService.listarTodas().stream()
                .filter(c -> c.getStatusCampanha() == Status.ATIVO)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nenhuma campanha ativa encontrada para teste"));

        Long id = campanhaAtiva.getIdCampanha();

        RegraNegocioException excecao = assertThrows(
                RegraNegocioException.class,
                () -> campanhaService.excluir(id)
        );

        assertTrue(excecao.getMessage().contains("Não é permitido excluir uma campanha com status ATIVO"));

        // Garante que a campanha continua existindo no banco
        assertTrue(campanhaRepository.existsById(id));
    }
}