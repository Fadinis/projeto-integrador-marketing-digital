package br.edu.eseg.marketing.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "landing_page")
public class LandingPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_landing_page")
    private Long idLandingPage;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_campanha", nullable = false)
    private Campanha campanha;

    @NotBlank
    @Column(name = "nome_landing_page", nullable = false, length = 150)
    private String nomeLandingPage;

    @NotBlank
    @Column(name = "tipo_pagina", nullable = false, length = 80)
    private String tipoPagina;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String produto;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long visitas = 0L;

    @NotNull
    @PositiveOrZero
    @Column(nullable = false)
    private Long conversoes = 0L;

    @NotNull
    @PositiveOrZero
    @Column(name = "tempo_medio_pagina_min", nullable = false, precision = 6, scale = 2)
    private BigDecimal tempoMedioPaginaMin = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_landing_page", nullable = false, length = 20)
    private Status statusLandingPage = Status.ATIVO;

    public LandingPage() {
    }

    public BigDecimal calcularTaxaConversao() {
        if (visitas == null || visitas == 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(conversoes)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(visitas), 2, RoundingMode.HALF_UP);
    }

    public Long getIdLandingPage() { return idLandingPage; }
    public void setIdLandingPage(Long idLandingPage) { this.idLandingPage = idLandingPage; }

    public Campanha getCampanha() { return campanha; }
    public void setCampanha(Campanha campanha) { this.campanha = campanha; }

    public String getNomeLandingPage() { return nomeLandingPage; }
    public void setNomeLandingPage(String nomeLandingPage) { this.nomeLandingPage = nomeLandingPage; }

    public String getTipoPagina() { return tipoPagina; }
    public void setTipoPagina(String tipoPagina) { this.tipoPagina = tipoPagina; }

    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }

    public Long getVisitas() { return visitas; }
    public void setVisitas(Long visitas) { this.visitas = visitas; }

    public Long getConversoes() { return conversoes; }
    public void setConversoes(Long conversoes) { this.conversoes = conversoes; }

    public BigDecimal getTempoMedioPaginaMin() { return tempoMedioPaginaMin; }
    public void setTempoMedioPaginaMin(BigDecimal tempoMedioPaginaMin) { this.tempoMedioPaginaMin = tempoMedioPaginaMin; }

    public Status getStatusLandingPage() { return statusLandingPage; }
    public void setStatusLandingPage(Status statusLandingPage) { this.statusLandingPage = statusLandingPage; }
}