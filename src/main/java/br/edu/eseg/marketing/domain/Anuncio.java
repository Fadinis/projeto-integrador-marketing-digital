package br.edu.eseg.marketing.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "anuncio")
public class Anuncio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_anuncio")
    private Long idAnuncio;

    @NotNull(message = "A campanha é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_campanha", nullable = false)
    private Campanha campanha;

    @NotBlank(message = "O nome do anúncio é obrigatório")
    @Column(name = "nome_anuncio", nullable = false, length = 150)
    private String nomeAnuncio;

    @NotBlank(message = "O formato é obrigatório")
    @Column(nullable = false, length = 60)
    private String formato;

    @NotBlank(message = "A versão do teste A/B é obrigatória")
    @Column(name = "versao_teste_ab", nullable = false, length = 5)
    private String versaoTesteAb;

    @NotBlank(message = "A linha criativa é obrigatória")
    @Column(name = "linha_criativa", nullable = false, length = 80)
    private String linhaCriativa;

    @NotNull(message = "O status do anúncio é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status_anuncio", nullable = false, length = 20)
    private Status statusAnuncio = Status.ATIVO;

    @NotNull(message = "A nota de qualidade é obrigatória")
    @DecimalMin(value = "0.0", message = "A nota mínima é 0")
    @DecimalMax(value = "10.0", message = "A nota máxima é 10")
    @Column(name = "nota_qualidade_criativo", nullable = false, precision = 4, scale = 2)
    private BigDecimal notaQualidadeCriativo;

    public Anuncio() {
    }

    public Long getIdAnuncio() { return idAnuncio; }
    public void setIdAnuncio(Long idAnuncio) { this.idAnuncio = idAnuncio; }

    public Campanha getCampanha() { return campanha; }
    public void setCampanha(Campanha campanha) { this.campanha = campanha; }

    public String getNomeAnuncio() { return nomeAnuncio; }
    public void setNomeAnuncio(String nomeAnuncio) { this.nomeAnuncio = nomeAnuncio; }

    public String getFormato() { return formato; }
    public void setFormato(String formato) { this.formato = formato; }

    public String getVersaoTesteAb() { return versaoTesteAb; }
    public void setVersaoTesteAb(String versaoTesteAb) { this.versaoTesteAb = versaoTesteAb; }

    public String getLinhaCriativa() { return linhaCriativa; }
    public void setLinhaCriativa(String linhaCriativa) { this.linhaCriativa = linhaCriativa; }

    public Status getStatusAnuncio() { return statusAnuncio; }
    public void setStatusAnuncio(Status statusAnuncio) { this.statusAnuncio = statusAnuncio; }

    public BigDecimal getNotaQualidadeCriativo() { return notaQualidadeCriativo; }
    public void setNotaQualidadeCriativo(BigDecimal notaQualidadeCriativo) { this.notaQualidadeCriativo = notaQualidadeCriativo; }
}