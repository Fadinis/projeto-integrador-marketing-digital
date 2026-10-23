package br.edu.eseg.marketing.domain;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conta")
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta")
    private Long idConta;

    @NotBlank(message = "O nome da conta é obrigatório")
    @Column(name = "nome_conta", nullable = false, length = 120)
    private String nomeConta;

    @NotBlank(message = "O segmento é obrigatório")
    @Column(nullable = false, length = 80)
    private String segmento;

    @NotBlank(message = "O produto principal é obrigatório")
    @Column(name = "produto_principal", nullable = false, length = 100)
    private String produtoPrincipal;

    @NotBlank(message = "O porte da empresa é obrigatório")
    @Column(name = "porte_empresa", nullable = false, length = 50)
    private String porteEmpresa;

    @NotNull(message = "O ano de início é obrigatório")
    @Min(value = 2000, message = "O ano de início deve ser a partir de 2000")
    @Column(name = "ano_inicio_cliente", nullable = false)
    private Integer anoInicioCliente;

    @NotNull(message = "O status da conta é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status_conta", nullable = false, length = 20)
    private Status statusConta = Status.ATIVO;

    @Valid
    @Embedded
    private Localizacao localizacao;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Campanha> campanhas = new ArrayList<>();

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmailMarketing> emailsMarketing = new ArrayList<>();

    public Conta() {
    }

    public Long getIdConta() { return idConta; }
    public void setIdConta(Long idConta) { this.idConta = idConta; }

    public String getNomeConta() { return nomeConta; }
    public void setNomeConta(String nomeConta) { this.nomeConta = nomeConta; }

    public String getSegmento() { return segmento; }
    public void setSegmento(String segmento) { this.segmento = segmento; }

    public String getProdutoPrincipal() { return produtoPrincipal; }
    public void setProdutoPrincipal(String produtoPrincipal) { this.produtoPrincipal = produtoPrincipal; }

    public String getPorteEmpresa() { return porteEmpresa; }
    public void setPorteEmpresa(String porteEmpresa) { this.porteEmpresa = porteEmpresa; }

    public Integer getAnoInicioCliente() { return anoInicioCliente; }
    public void setAnoInicioCliente(Integer anoInicioCliente) { this.anoInicioCliente = anoInicioCliente; }

    public Status getStatusConta() { return statusConta; }
    public void setStatusConta(Status statusConta) { this.statusConta = statusConta; }

    public Localizacao getLocalizacao() { return localizacao; }
    public void setLocalizacao(Localizacao localizacao) { this.localizacao = localizacao; }

    public List<Campanha> getCampanhas() { return campanhas; }
    public void setCampanhas(List<Campanha> campanhas) { this.campanhas = campanhas; }

    public List<EmailMarketing> getEmailsMarketing() { return emailsMarketing; }
    public void setEmailsMarketing(List<EmailMarketing> emailsMarketing) { this.emailsMarketing = emailsMarketing; }
}