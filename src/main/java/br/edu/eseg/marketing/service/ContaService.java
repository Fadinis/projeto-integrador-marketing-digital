package br.edu.eseg.marketing.service;

import br.edu.eseg.marketing.domain.Campanha;
import br.edu.eseg.marketing.domain.Conta;
import br.edu.eseg.marketing.domain.Status;
import br.edu.eseg.marketing.exception.RecursoNaoEncontradoException;
import br.edu.eseg.marketing.exception.RegraNegocioException;
import br.edu.eseg.marketing.repository.CampanhaRepository;
import br.edu.eseg.marketing.repository.ContaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final CampanhaRepository campanhaRepository;

    public ContaService(ContaRepository contaRepository, CampanhaRepository campanhaRepository) {
        this.contaRepository = contaRepository;
        this.campanhaRepository = campanhaRepository;
    }

    @Transactional(readOnly = true)
    public List<Conta> listarTodas() {
        return contaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Conta> listarContasAtivas() {
        return contaRepository.findByStatusConta(Status.ATIVO);
    }

    @Transactional(readOnly = true)
    public Conta buscarPorId(Long id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Conta não encontrada com o ID: " + id));
    }

    @Transactional
    public Conta salvar(Conta conta) {
        if (conta.getStatusConta() != Status.ATIVO && conta.getStatusConta() != Status.INATIVO) {
            throw new RegraNegocioException("O statsu de uma Conta deve ser apenas ATIVO ou INATIVO.");
        }

        // Regar: Não permite inativar conta se houver campanhas ativas vinculadas
        if (conta.getIdConta() != null && conta.getStatusConta() != Status.INATIVO) {
            List<Campanha> campanhasDaConta = campanhaRepository.findByContaIdConta(conta.getIdConta());
            boolean possuiCampanhaAtiva = campanhasDaConta.stream()
                    .anyMatch(c -> c.getStatusCampanha() == Status.ATIVO);
            if (possuiCampanhaAtiva) {
                throw new RegraNegocioException(
                        "Não é possível inativar esta conta pois ela possui campanhas com status ATIVO."
                );
            }
        }
        return contaRepository.save(conta);
    }
}
