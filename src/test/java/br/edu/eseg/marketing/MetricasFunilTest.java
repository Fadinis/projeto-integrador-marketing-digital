package br.edu.eseg.marketing;

import br.edu.eseg.marketing.domain.ResumoFunil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MetricasFunilTest {

    @Test
    @DisplayName("Deve calcular o CTR corretamente com base em cliques e impressões")
    void deveCalcularCtrCorretamente() {
        ResumoFunil funil = new ResumoFunil();
        funil.setImpressoes(10000L);
        funil.setCliques(500L);

        BigDecimal ctr = funil.calcularCTR();
        // (500 / 10000) * 100 = 5.00%
        assertEquals(0, new BigDecimal("5.00").compareTo(ctr));
    }

    @Test
    @DisplayName("Deve retornar CTR zero quando impressões forem zero para evitar divisão por zero")
    void deveRetornarCtrZeroSeImpressoesZero() {
        ResumoFunil funil = new ResumoFunil();
        funil.setImpressoes(0L);
        funil.setCliques(100L);

        assertEquals(BigDecimal.ZERO, funil.calcularCTR());
    }

    @Test
    @DisplayName("Deve calcular o ROAS corretamente dividindo receita por custo")
    void deveCalcularRoasCorretamente() {
        ResumoFunil funil = new ResumoFunil();
        funil.setCusto(new BigDecimal("100.00"));
        funil.setReceita(new BigDecimal("450.00"));

        BigDecimal roas = funil.calcularROAS();
        // 450 / 100 = 4.50x
        assertEquals(0, new BigDecimal("4.50").compareTo(roas));
    }
}