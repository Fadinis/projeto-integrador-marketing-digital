package br.edu.eseg.marketing.service;

import br.edu.eseg.marketing.domain.Anuncio;
import br.edu.eseg.marketing.domain.Campanha;
import br.edu.eseg.marketing.domain.Conta;
import br.edu.eseg.marketing.domain.Status;
import br.edu.eseg.marketing.exception.RecursoNaoEncontradoException;
import br.edu.eseg.marketing.exception.RegraNegocioException;
import br.edu.eseg.marketing.repository.AnuncioRepository;
import br.edu.eseg.marketing.repository.CampanhaRepository;
import br.edu.eseg.marketing.repository.ContaRepository;
import br.edu.eseg.marketing.repository.DesempenhoDiarioRepository;
import br.edu.eseg.marketing.repository.ResumoFunilRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class CampanhaService {

    private final CampanhaRepository campanhaRepository;
    private final ContaRepository contaRepository;
    private final AnuncioRepository anuncioRepository;
    private final ResumoFunilRepository resumoFunilRepository;
    private final DesempenhoDiarioRepository desempenhoDiarioRepository;

    public CampanhaService(
            CampanhaRepository campanhaRepository,
            ContaRepository contaRepository,
            AnuncioRepository anuncioRepository,
            ResumoFunilRepository resumoFunilRepository,
            DesempenhoDiarioRepository desempenhoDiarioRepository) {
        this.campanhaRepository = campanhaRepository;
        this.contaRepository = contaRepository;
        this.anuncioRepository = anuncioRepository;
        this.resumoFunilRepository = resumoFunilRepository;
        this.desempenhoDiarioRepository = desempenhoDiarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Campanha> listarTodas() {
        return campanhaRepository.listarTodasComConta();
    }

    @Transactional(readOnly = true)
    public Campanha buscarPorId(Long id) {
        return campanhaRepository.buscarPorIdComConta(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Campanha não encontrada com o ID: " + id));
    }

    @Transactional
    public Campanha salvar(Campanha dadosCampanha) {
        validarDatasCampanha(dadosCampanha);

        Conta conta = contaRepository.findById(dadosCampanha.getConta().getIdConta())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta anunciante não encontrada."));

        // Regra: Conta INATIVO nao pode receber campanhas ATIVO
        if (conta.getStatusConta() == Status.INATIVO && dadosCampanha.getStatusCampanha() == Status.ATIVO) {
            throw new RegraNegocioException(
                    "Não é permitido criar ou manter uma campanha ATIVA para uma Conta que está INATIVA."
            );
        }

        // Se for edicao de campanha existente, valida a transicao de status
        if (dadosCampanha.getIdCampanha() != null) {
            Campanha existente = buscarPorId(dadosCampanha.getIdCampanha());
            try {
                existente.alterarStatus(dadosCampanha.getStatusCampanha());
            } catch (IllegalStateException | IllegalArgumentException ex) {
                throw new RegraNegocioException(ex.getMessage());
            }

            existente.setConta(conta);
            existente.setNomeCampanha(dadosCampanha.getNomeCampanha());
            existente.setCanal(dadosCampanha.getCanal());
            existente.setObjetivo(dadosCampanha.getObjetivo());
            existente.setSegmento(dadosCampanha.getSegmento());
            existente.setProduto(dadosCampanha.getProduto());
            existente.setPosicaoFunil(dadosCampanha.getPosicaoFunil());
            existente.setAbrangenciaGeografica(dadosCampanha.getAbrangenciaGeografica());
            existente.setDataInicio(dadosCampanha.getDataInicio());
            existente.setDataFim(dadosCampanha.getDataFim());
            existente.setOrcamentoPlanejado(dadosCampanha.getOrcamentoPlanejado());

            sincronizarAnunciosComStatusDaCampanha(existente);
            return campanhaRepository.save(existente);
        }

        dadosCampanha.setConta(conta);
        return campanhaRepository.save(dadosCampanha);
    }

    @Transactional
    public void alterarStatus(Long idCampanha, Status novoStatus) {
        Campanha campanha = buscarPorId(idCampanha);

        if (novoStatus == Status.ATIVO && campanha.getConta().getStatusConta() == Status.INATIVO) {
            throw new RegraNegocioException("Não é possível ativar a campanha pois a Conta vinculada está INATIVA.");
        }

        try {
            campanha.alterarStatus(novoStatus);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            throw new RegraNegocioException(ex.getMessage());
        }

        sincronizarAnunciosComStatusDaCampanha(campanha);
        campanhaRepository.save(campanha);
    }

    @Transactional
    public void excluir(Long idCampanha) {
        Campanha campanha = buscarPorId(idCampanha);
        if (campanha.getStatusCampanha() == Status.ATIVO) {
            throw new RegraNegocioException(
                    "Não é permitido excluir uma campanha com status ATIVO. Pause ou encerre a campanha antes de excluí-la."
            );
        }
        campanhaRepository.delete(campanha);
    }

    // Requisito Funcional Novo (RF05): Calculo do custo real acumulado e controlo de orcamento
    @Transactional(readOnly = true)
    public BigDecimal calcularCustoAcumulado(Long idCampanha) {
        BigDecimal custoResumo = resumoFunilRepository.somarCustoPorCampanha(idCampanha);
        if (custoResumo != null && custoResumo.compareTo(BigDecimal.ZERO) > 0) {
            return custoResumo;
        }
        BigDecimal custoDiario = desempenhoDiarioRepository.somarCustoDiarioPorCampanha(idCampanha);
        return custoDiario != null ? custoDiario : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public boolean isOrcamentoEstourado(Campanha campanha) {
        if (campanha.getOrcamentoPlanejado() == null || campanha.getOrcamentoPlanejado().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        BigDecimal custoAcumulado = calcularCustoAcumulado(campanha.getIdCampanha());
        return custoAcumulado.compareTo(campanha.getOrcamentoPlanejado()) >= 0;
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularPercentualOrcamento(Campanha campanha) {
        if (campanha.getOrcamentoPlanejado() == null || campanha.getOrcamentoPlanejado().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal custoAcumulado = calcularCustoAcumulado(campanha.getIdCampanha());
        return custoAcumulado.multiply(BigDecimal.valueOf(100))
                .divide(campanha.getOrcamentoPlanejado(), 2, RoundingMode.HALF_UP);
    }

    private void validarDatasCampanha(Campanha campanha) {
        if (campanha.getDataInicio() != null && campanha.getDataFim() != null) {
            if (campanha.getDataFim().isBefore(campanha.getDataInicio())) {
                throw new RegraNegocioException(
                        "Data inválida: a data de fim (" + campanha.getDataFim() +
                                ") não pode ser anterior à data de início (" + campanha.getDataInicio() + ")."
                );
            }
        }
    }

    // Se a campanha for ENCERRADO ou PAUSADO, reflete nos anuncios ativos dela
    private void sincronizarAnunciosComStatusDaCampanha(Campanha campanha) {
        if (campanha.getStatusCampanha() == Status.ENCERRADO) {
            List<Anuncio> anuncios = anuncioRepository.findByCampanhaIdCampanha(campanha.getIdCampanha());
            for (Anuncio anuncio : anuncios) {
                anuncio.setStatusAnuncio(Status.ENCERRADO);
            }
            anuncioRepository.saveAll(anuncios);
        } else if (campanha.getStatusCampanha() == Status.PAUSADO) {
            List<Anuncio> anuncios = anuncioRepository.findByCampanhaIdCampanha(campanha.getIdCampanha());
            for (Anuncio anuncio : anuncios) {
                if (anuncio.getStatusAnuncio() == Status.ATIVO) {
                    anuncio.setStatusAnuncio(Status.PAUSADO);
                }
            }
            anuncioRepository.saveAll(anuncios);
        }
    }
}