package br.edu.eseg.marketing.domain;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "publico_alvo")
public class PublicoAlvo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publico")
    private Long idPublico;

    @NotBlank(message = "O nome do público é obrigatório")
    @Column(name = "nome_publico", nullable = false, length = 120)
    private String nomePublico;

    @NotBlank(message = "A faixa etária é obrigatória")
    @Column(name = "faixa_etaria", nullable = false, length = 20)
    private String faixaEtaria;

    @NotBlank(message = "O género é obrigatório")
    @Column(nullable = false, length = 30)
    private String genero;

    @NotBlank(message = "O tipo de segmentação é obrigatório")
    @Column(name = "tipo_segmentacao", nullable = false, length = 80)
    private String tipoSegmentacao;

    @NotBlank(message = "A intenção de compra é obrigatória")
    @Column(name = "intencao_compra", nullable = false, length = 30)
    private String intencaoCompra;

    @NotNull(message = "O tamanho estimado é obrigatório")
    @Positive(message = "O tamanho estimado deve ser maior que zero")
    @Column(name = "tamanho_estimado_publico", nullable = false)
    private Long tamanhoEstimadoPublico;

    @Valid
    @Embedded
    private Localizacao localizacao;

    public PublicoAlvo() {
    }

    public Long getIdPublico() { return idPublico; }
    public void setIdPublico(Long idPublico) { this.idPublico = idPublico; }

    public String getNomePublico() { return nomePublico; }
    public void setNomePublico(String nomePublico) { this.nomePublico = nomePublico; }

    public String getFaixaEtaria() { return faixaEtaria; }
    public void setFaixaEtaria(String faixaEtaria) { this.faixaEtaria = faixaEtaria; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getTipoSegmentacao() { return tipoSegmentacao; }
    public void setTipoSegmentacao(String tipoSegmentacao) { this.tipoSegmentacao = tipoSegmentacao; }

    public String getIntencaoCompra() { return intencaoCompra; }
    public void setIntencaoCompra(String intencaoCompra) { this.intencaoCompra = intencaoCompra; }

    public Long getTamanhoEstimadoPublico() { return tamanhoEstimadoPublico; }
    public void setTamanhoEstimadoPublico(Long tamanhoEstimadoPublico) { this.tamanhoEstimadoPublico = tamanhoEstimadoPublico; }

    public Localizacao getLocalizacao() { return localizacao; }
    public void setLocalizacao(Localizacao localizacao) { this.localizacao = localizacao; }
}