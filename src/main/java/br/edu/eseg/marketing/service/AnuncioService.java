package br.edu.eseg.marketing.service;

import br.edu.eseg.marketing.domain.Anuncio;
import br.edu.eseg.marketing.domain.Campanha;
import br.edu.eseg.marketing.domain.Status;
import br.edu.eseg.marketing.exception.RecursoNaoEncontradoException;
import br.edu.eseg.marketing.exception.RegraNegocioException;
import br.edu.eseg.marketing.repository.AnuncioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;
    private final CampanhaService campanhaService;

    public AnuncioService(AnuncioRepository anuncioRepository, CampanhaService campanhaService) {
        this.anuncioRepository = anuncioRepository;
        this.campanhaService = campanhaService;
    }

    @Transactional(readOnly = true)
    public List<Anuncio> listarTodos() {
        return anuncioRepository.listarTodosComCampanha();
    }

    @Transactional(readOnly = true)
    public List<Anuncio> listarPorCampanha(Long idCampanha) {
        return anuncioRepository.findByCampanhaIdCampanha(idCampanha);
    }

    @Transactional(readOnly = true)
    public Anuncio buscarPorId(Long id) {
        return anuncioRepository.buscarPorIdComCampanha(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Anúncio não encontrado com o ID: " + id));
    }

    @Transactional
    public Anuncio salvar(Anuncio anuncio) {
        Campanha campanha = campanhaService.buscarPorId(anuncio.getCampanha().getIdCampanha());

        // Validacao de transicao de status se o anuncio ja existir
        if (anuncio.getIdAnuncio() != null) {
            Anuncio existente = buscarPorId(anuncio.getIdAnuncio());
            if (!existente.getStatusAnuncio().podeTransitarPara(anuncio.getStatusAnuncio())) {
                throw new RegraNegocioException(
                        "Transição inválida: um anúncio " + existente.getStatusAnuncio() +
                                " não pode mudar para " + anuncio.getStatusAnuncio() + "."
                );
            }
        }

        // Regra 1: Nao permite anuncio ATIVO em campanha PAUSADO ou ENCERRADO
        if (anuncio.getStatusAnuncio() == Status.ATIVO && campanha.getStatusCampanha() != Status.ATIVO) {
            throw new RegraNegocioException(
                    "Não é possível ativar este anúncio pois a campanha '" + campanha.getNomeCampanha() +
                            "' está com status " + campanha.getStatusCampanha() + "."
            );
        }

        // Regra 2 (Requisito Novo RF05): Bloqueia ativacao de anuncio se o orcamento da campanha estourou
        if (anuncio.getStatusAnuncio() == Status.ATIVO && campanhaService.isOrcamentoEstourado(campanha)) {
            throw new RegraNegocioException(
                    "Orçamento esgotado! A campanha '" + campanha.getNomeCampanha() +
                            "' já atingiu ou ultrapassou o orçamento planejado de R$ " + campanha.getOrcamentoPlanejado() + "."
            );
        }

        anuncio.setCampanha(campanha);
        return anuncioRepository.save(anuncio);
    }

    @Transactional
    public void excluir(Long idAnuncio) {
        Anuncio anuncio = buscarPorId(idAnuncio);
        anuncioRepository.delete(anuncio);
    }
}