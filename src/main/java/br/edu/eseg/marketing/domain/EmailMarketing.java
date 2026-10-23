package br.edu.eseg.marketing.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Entity
@Table(name = "email_marketing")
public class EmailMarketing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_email")
    private Long idEmail;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_conta", nullable = false)
    private Conta conta;

    @NotBlank
    @Column(name = "tipo_email", nullable = false, length = 80)
    private String tipoEmail;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long envios = 0L;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long aberturas = 0L;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long cliques = 0L;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long conversoes = 0L;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long descadastros = 0L;

    @NotNull
    @Column(nullable = false)
    private LocalDate data;

    @NotNull
    @PositiveOrZero
    @Column(name = "custo_envio", nullable = false, precision = 15, scale = 2)
    private BigDecimal custoEnvio = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    @Column(name = "receita_atribuida", nullable = false, precision = 15, scale = 2)
    private BigDecimal receitaAtribuida = BigDecimal.ZERO;

    public EmailMarketing() {
    }

    public BigDecimal calcularTaxaAbertura() {
        if (envios == null || envios == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(aberturas)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(envios), 2, RoundingMode.HALF_UP);
    }

    public Long getIdEmail() { return idEmail; }
    public void setIdEmail(Long idEmail) { this.idEmail = idEmail; }

    public Conta getConta() { return conta; }
    public void setConta(Conta conta) { this.conta = conta; }

    public String getTipoEmail() { return tipoEmail; }
    public void setTipoEmail(String tipoEmail) { this.tipoEmail = tipoEmail; }

    public Long getEnvios() { return envios; }
    public void setEnvios(Long envios) { this.envios = envios; }

    public Long getAberturas() { return aberturas; }
    public void setAberturas(Long aberturas) { this.aberturas = aberturas; }

    public Long getCliques() { return cliques; }
    public void setCliques(Long cliques) { this.cliques = cliques; }

    public Long getConversoes() { return conversoes; }
    public void setConversoes(Long conversoes) { this.conversoes = conversoes; }

    public Long getDescadastros() { return descadastros; }
    public void setDescadastros(Long descadastros) { this.descadastros = descadastros; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public BigDecimal getCustoEnvio() { return custoEnvio; }
    public void setCustoEnvio(BigDecimal custoEnvio) { this.custoEnvio = custoEnvio; }

    public BigDecimal getReceitaAtribuida() { return receitaAtribuida; }
    public void setReceitaAtribuida(BigDecimal receitaAtribuida) { this.receitaAtribuida = receitaAtribuida; }
}