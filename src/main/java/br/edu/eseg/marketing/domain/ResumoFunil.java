package br.edu.eseg.marketing.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "resumo_funil")
public class ResumoFunil extends MetricasFunil {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_funil")
    private Long idFunil;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_campanha", nullable = false)
    private Campanha campanha;

    public ResumoFunil() {
    }

    public Long getIdFunil() { return idFunil; }
    public void setIdFunil(Long idFunil) { this.idFunil = idFunil; }

    public Campanha getCampanha() { return campanha; }
    public void setCampanha(Campanha campanha) { this.campanha = campanha; }
}