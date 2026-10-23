package br.edu.eseg.marketing.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "campanha")
public class Campanha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_campanha")
    private Long idCampanha;

    @NotNull(message = "A conta anunciante é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_conta", nullable = false)
    private Conta conta;

    @NotBlank(message = "O nome da campanha é obrigatório")
    @Column(name = "nome_campanha", nullable = false, length = 150)
    private String nomeCampanha;

    @NotBlank(message = "O canal é obrigatório")
    @Column(nullable = false, length = 60)
    private String canal;

    @NotBlank(message = "O objetivo é obrigatório")
    @Column(nullable = false, length = 80)
    private String objetivo;

    @NotBlank(message = "O segmento é obrigatório")
    @Column(nullable = false, length = 80)
    private String segmento;

    @NotBlank(message = "O produto é obrigatório")
    @Column(nullable = false, length = 100)
    private String produto;

    @NotNull(message = "O status da campanha é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status_campanha", nullable = false, length = 20)
    private Status statusCampanha = Status.ATIVO;

    @NotBlank(message = "A posição no funil é obrigatória")
    @Column(name = "posicao_funil", nullable = false, length = 50)
    private String posicaoFunil;

    @NotBlank(message = "A abrangência geográfica é obrigatória")
    @Column(name = "abrangencia_geografica", nullable = false, length = 60)
    private String abrangenciaGeografica;

    @NotNull(message = "A data de início é obrigatória")
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @NotNull(message = "A data de fim é obrigatória")
    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @NotNull(message = "O orçamento planejado é obrigatório")
    @PositiveOrZero(message = "O orçamento não pode ser negativo")
    @Column(name = "orcamento_planejado", nullable = false, precision = 15, scale = 2)
    private BigDecimal orcamentoPlanejado;

    @OneToMany(mappedBy = "campanha", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Anuncio> anuncios = new ArrayList<>();

    @OneToMany(mappedBy = "campanha", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LandingPage> landingPages = new ArrayList<>();

    @OneToMany(mappedBy = "campanha", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResumoFunil> resumosFunil = new ArrayList<>();

    public Campanha() {
    }

    // Regra de negocio do diagrama UML: controla a transicao de status
    public void alterarStatus(Status novoStatus) {
        if (novoStatus == null) {
            throw new IllegalArgumentException("O novo status não pode ser nulo.");
        }
        if (this.statusCampanha != null && !this.statusCampanha.podeTransitarPara(novoStatus)) {
            throw new IllegalStateException(
                    "Transição de status inválida: uma campanha com status " + this.statusCampanha +
                            " não pode ser alterada para " + novoStatus + "."
            );
        }
        this.statusCampanha = novoStatus;
    }

    public Long getIdCampanha() { return idCampanha; }
    public void setIdCampanha(Long idCampanha) { this.idCampanha = idCampanha; }

    public Conta getConta() { return conta; }
    public void setConta(Conta conta) { this.conta = conta; }

    public String getNomeCampanha() { return nomeCampanha; }
    public void setNomeCampanha(String nomeCampanha) { this.nomeCampanha = nomeCampanha; }

    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }

    public String getObjetivo() { return objetivo; }
    public void setObjetivo(String objetivo) { this.objetivo = objetivo; }

    public String getSegmento() { return segmento; }
    public void setSegmento(String segmento) { this.segmento = segmento; }

    public String getProduto() { return produto; }
    public void setProduto(String produto) { this.produto = produto; }

    public Status getStatusCampanha() { return statusCampanha; }
    public void setStatusCampanha(Status statusCampanha) { this.statusCampanha = statusCampanha; }

    public String getPosicaoFunil() { return posicaoFunil; }
    public void setPosicaoFunil(String posicaoFunil) { this.posicaoFunil = posicaoFunil; }

    public String getAbrangenciaGeografica() { return abrangenciaGeografica; }
    public void setAbrangenciaGeografica(String abrangenciaGeografica) { this.abrangenciaGeografica = abrangenciaGeografica; }

    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate dataInicio) { this.dataInicio = dataInicio; }

    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate dataFim) { this.dataFim = dataFim; }

    public BigDecimal getOrcamentoPlanejado() { return orcamentoPlanejado; }
    public void setOrcamentoPlanejado(BigDecimal orcamentoPlanejado) { this.orcamentoPlanejado = orcamentoPlanejado; }

    public List<Anuncio> getAnuncios() { return anuncios; }
    public void setAnuncios(List<Anuncio> anuncios) { this.anuncios = anuncios; }

    public List<LandingPage> getLandingPages() { return landingPages; }
    public void setLandingPages(List<LandingPage> landingPages) { this.landingPages = landingPages; }

    public List<ResumoFunil> getResumosFunil() { return resumosFunil; }
    public void setResumosFunil(List<ResumoFunil> resumosFunil) { this.resumosFunil = resumosFunil; }
}