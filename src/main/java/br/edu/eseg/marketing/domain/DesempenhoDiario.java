package br.edu.eseg.marketing.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "desempenho_diario")
public class DesempenhoDiario extends MetricasFunil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_desempenho")
    private Long idDesempenho;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_anuncio", nullable = false)
    private Anuncio anuncio;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_publico", nullable = false)
    private PublicoAlvo publicoAlvo;

    @NotBlank
    @Column(nullable = false, length = 40)
    private String dispositivo;

    public DesempenhoDiario() {
    }

    public Long getIdDesempenho() { return idDesempenho; }
    public void setIdDesempenho(Long idDesempenho) { this.idDesempenho = idDesempenho; }

    public Anuncio getAnuncio() { return anuncio; }
    public void setAnuncio(Anuncio anuncio) { this.anuncio = anuncio; }

    public PublicoAlvo getPublicoAlvo() { return publicoAlvo; }
    public void setPublicoAlvo(PublicoAlvo publicoAlvo) { this.publicoAlvo = publicoAlvo; }

    public String getDispositivo() { return dispositivo; }
    public void setDispositivo(String dispositivo) { this.dispositivo = dispositivo; }
}