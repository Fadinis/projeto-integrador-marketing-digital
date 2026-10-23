package br.edu.eseg.marketing.domain;

public enum Status {
    ATIVO,
    PAUSADO,
    ENCERRADO,
    INATIVO,
    ARQUIVADO;

    public boolean podeTransitarPara(Status novoStatus) {
        if (this == novoStatus) {
            return true;
        }
        // Uma campanha ou anuncio ENCERRADO ou ARQUIVADO não pode voltar a ficar ATIVO ou PAUSADO
        if (this == ENCERRADO || this == ARQUIVADO) {
            return false;
        }
        return true;
    }
}
