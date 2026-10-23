package br.edu.eseg.marketing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@MappedSuperclass
public abstract class MetricasFunil {

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    protected Long impressoes = 0L;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    protected Long cliques = 0L;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    protected Long leads = 0L;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    protected Long oportunidades = 0L;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    protected Long vendas = 0L;

    @NotNull
    @Column(nullable = false)
    protected LocalDate data;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 15, scale = 2)
    protected BigDecimal custo = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false, precision = 15, scale = 2)
    protected BigDecimal receita = BigDecimal.ZERO;

    // Calcula CTR em percentagem: (cliques / impressoes) * 100
    public BigDecimal calcularCTR() {
        if (impressoes == null || impressoes == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(cliques)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(impressoes), 2, RoundingMode.HALF_UP);
    }

    // Calcula ROAS: receita / custo
    public BigDecimal calcularROAS() {
        if (custo == null || custo.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return receita.divide(custo, 2, RoundingMode.HALF_UP);
    }

    public Long getImpressoes() { return impressoes; }
    public void setImpressoes(Long impressoes) { this.impressoes = impressoes; }

    public Long getCliques() { return cliques; }
    public void setCliques(Long cliques) { this.cliques = cliques; }

    public Long getLeads() { return leads; }
    public void setLeads(Long leads) { this.leads = leads; }

    public Long getOportunidades() { return oportunidades; }
    public void setOportunidades(Long oportunidades) { this.oportunidades = oportunidades; }

    public Long getVendas() { return vendas; }
    public void setVendas(Long vendas) { this.vendas = vendas; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public BigDecimal getCusto() { return custo; }
    public void setCusto(BigDecimal custo) { this.custo = custo; }

    public BigDecimal getReceita() { return receita; }
    public void setReceita(BigDecimal receita) { this.receita = receita; }
}