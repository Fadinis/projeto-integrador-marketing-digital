package br.edu.eseg.marketing;

import br.edu.eseg.marketing.domain.Campanha;
import br.edu.eseg.marketing.domain.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CampanhaTest {

    @Test
    @DisplayName("Deve permitir transição válida de ATIVO para PAUSADO")
    void devePermitirTransicaoValida() {
        Campanha campanha = new Campanha();
        campanha.setStatusCampanha(Status.ATIVO);

        assertDoesNotThrow(() -> campanha.alterarStatus(Status.PAUSADO));
        assertEquals(Status.PAUSADO, campanha.getStatusCampanha());
    }

    @Test
    @DisplayName("Deve bloquear transição inválida a partir de status ENCERRADO")
    void deveBloquearTransicaoDeEncerrado() {
        Campanha campanha = new Campanha();
        campanha.setStatusCampanha(Status.ENCERRADO);

        IllegalStateException excecao = assertThrows(
                IllegalStateException.class,
                () -> campanha.alterarStatus(Status.ATIVO)
        );

        assertTrue(excecao.getMessage().contains("Transição de status inválida"));
    }
}