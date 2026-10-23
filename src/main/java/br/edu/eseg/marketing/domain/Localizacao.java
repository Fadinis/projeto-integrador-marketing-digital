package br.edu.eseg.marketing.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Embeddable
public class Localizacao {

    @NotBlank(message = "A cidade é obrigatória")
    @Column(nullable = false, length = 100)
    private String cidade;

    @NotBlank(message = "O estado (UF) é obrigatorio")
    @Size(min = 2, max = 2, message = "O estado deve ter 2 letras (ex: SP)")
    @Column(nullable = false, length = 2)
    private String estado;

    @NotBlank(message = "A região é obrigatória")
    @Column(nullable = false, length = 40)
    private String regiao;

    public Localizacao() {
    }

    public Localizacao(String cidade, String estado, String regiao) {
        this.cidade = cidade;
        this.estado = estado;
        this.regiao = regiao;
    }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getRegiao() { return regiao; }
    public void setRegiao(String regiao) { this.regiao = regiao; }
}
