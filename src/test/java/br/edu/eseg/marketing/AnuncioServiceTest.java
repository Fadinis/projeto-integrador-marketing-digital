package br.edu.eseg.marketing;

import br.edu.eseg.marketing.domain.Anuncio;
import br.edu.eseg.marketing.domain.Campanha;
import br.edu.eseg.marketing.domain.Status;
import br.edu.eseg.marketing.exception.RegraNegocioException;
import br.edu.eseg.marketing.repository.CampanhaRepository;
import br.edu.eseg.marketing.service.AnuncioService;
import br.edu.eseg.marketing.service.CampanhaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AnuncioServiceTest {

    @Autowired
    private AnuncioService anuncioService;

    @Autowired
    private CampanhaService campanhaService;

    @Autowired
    private CampanhaRepository campanhaRepository;

    @Test
    @DisplayName("RF05: Deve bloquear ativação de novo anúncio quando orçamento da campanha estiver esgotado")
    void deveBloquearAtivacaoAnuncioQuandoOrcamentoEstourado() {
        // Encontra uma campanha existente da base migrada
        Campanha campanha = campanhaService.listarTodas().stream()
                .filter(c -> c.getStatusCampanha() == Status.ATIVO)
                .findFirst()
                .orElseThrow();

        // Simula orcamento muito baixo em relacao aos custos existentes (forcando orcamento estourado)
        BigDecimal orcamentoOriginal = campanha.getOrcamentoPlanejado();
        try {
            campanha.setOrcamentoPlanejado(new BigDecimal("10.00"));
            campanhaRepository.save(campanha);

            assertTrue(campanhaService.isOrcamentoEstourado(campanha),
                    "A campanha deve ser detectada como orcamento estourado");

            Anuncio novoAnuncio = new Anuncio();
            novoAnuncio.setCampanha(campanha);
            novoAnuncio.setNomeAnuncio("Criativo Teste Estouro");
            novoAnuncio.setFormato("Banner");
            novoAnuncio.setVersaoTesteAb("A");
            novoAnuncio.setLinhaCriativa("Promocional");
            novoAnuncio.setStatusAnuncio(Status.ATIVO);
            novoAnuncio.setNotaQualidadeCriativo(new BigDecimal("8.00"));

            RegraNegocioException ex = assertThrows(
                    RegraNegocioException.class,
                    () -> anuncioService.salvar(novoAnuncio)
            );

            assertTrue(ex.getMessage().contains("Orçamento esgotado!"),
                    "A mensagem de erro deve indicar que o orcamento foi esgotado");

        } finally {
            // Restaura o orcamento original da campanha
            campanha.setOrcamentoPlanejado(orcamentoOriginal);
            campanhaRepository.save(campanha);
        }
    }

    @Test
    @DisplayName("Deve bloquear anúncio ATIVO vinculado a uma campanha PAUSADA")
    void deveBloquearAnuncioAtivoEmCampanhaPausada() {
        Campanha campanha = campanhaService.listarTodas().stream()
                .filter(c -> c.getStatusCampanha() == Status.ATIVO)
                .findFirst()
                .orElseThrow();

        Status statusOriginal = campanha.getStatusCampanha();
        try {
            campanha.alterarStatus(Status.PAUSADO);
            campanhaRepository.save(campanha);

            Anuncio anuncio = new Anuncio();
            anuncio.setCampanha(campanha);
            anuncio.setNomeAnuncio("Criativo Campanha Pausada");
            anuncio.setFormato("Vídeo");
            anuncio.setVersaoTesteAb("B");
            anuncio.setLinhaCriativa("Educativo");
            anuncio.setStatusAnuncio(Status.ATIVO);
            anuncio.setNotaQualidadeCriativo(new BigDecimal("9.00"));

            RegraNegocioException ex = assertThrows(
                    RegraNegocioException.class,
                    () -> anuncioService.salvar(anuncio)
            );

            assertTrue(ex.getMessage().contains("Não é possível ativar este anúncio pois a campanha"),
                    "Deve impedir anúncio ativo em campanha que não está ativa");
        } finally {
            campanha.setStatusCampanha(statusOriginal);
            campanhaRepository.save(campanha);
        }
    }
}
