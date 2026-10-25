package br.edu.eseg.marketing.controller;

import br.edu.eseg.marketing.domain.Campanha;
import br.edu.eseg.marketing.domain.Status;
import br.edu.eseg.marketing.exception.RegraNegocioException;
import br.edu.eseg.marketing.repository.LandingPageRepository;
import br.edu.eseg.marketing.repository.ResumoFunilRepository;
import br.edu.eseg.marketing.service.AnuncioService;
import br.edu.eseg.marketing.service.CampanhaService;
import br.edu.eseg.marketing.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/campanhas")
public class CampanhaController {

    private final CampanhaService campanhaService;
    private final ContaService contaService;
    private final AnuncioService anuncioService;
    private final ResumoFunilRepository resumoFunilRepository;
    private final LandingPageRepository landingPageRepository;

    public CampanhaController(
            CampanhaService campanhaService,
            ContaService contaService,
            AnuncioService anuncioService,
            ResumoFunilRepository resumoFunilRepository,
            LandingPageRepository landingPageRepository) {
        this.campanhaService = campanhaService;
        this.contaService = contaService;
        this.anuncioService = anuncioService;
        this.resumoFunilRepository = resumoFunilRepository;
        this.landingPageRepository = landingPageRepository;
    }

    @GetMapping
    public String listar(Model model) {
        List<Campanha> campanhas = campanhaService.listarTodas();

        Map<Long, BigDecimal> custosPorCampanha = new HashMap<>();
        Map<Long, BigDecimal> percentuaisOrcamento = new HashMap<>();
        Map<Long, Boolean> orcamentosEstourados = new HashMap<>();

        for (Campanha c : campanhas) {
            custosPorCampanha.put(c.getIdCampanha(), campanhaService.calcularCustoAcumulado(c.getIdCampanha()));
            percentuaisOrcamento.put(c.getIdCampanha(), campanhaService.calcularPercentualOrcamento(c));
            orcamentosEstourados.put(c.getIdCampanha(), campanhaService.isOrcamentoEstourado(c));
        }

        model.addAttribute("campanhas", campanhas);
        model.addAttribute("custosPorCampanha", custosPorCampanha);
        model.addAttribute("percentuaisOrcamento", percentuaisOrcamento);
        model.addAttribute("orcamentosEstourados", orcamentosEstourados);
        model.addAttribute("statusOpcoes", List.of(Status.ATIVO, Status.PAUSADO, Status.ENCERRADO));

        return "campanhas/lista";
    }

    @GetMapping("/nova")
    public String exibirFormularioNova(Model model) {
        Campanha campanha = new Campanha();
        campanha.setStatusCampanha(Status.ATIVO);
        prepararFormulario(model, campanha);
        return "campanhas/formulario";
    }

    @GetMapping("/{id}/editar")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        Campanha campanha = campanhaService.buscarPorId(id);
        prepararFormulario(model, campanha);
        return "campanhas/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("campanha") Campanha campanha,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            prepararFormulario(model, campanha);
            return "campanhas/formulario";
        }

        try {
            campanhaService.salvar(campanha);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Campanha guardada com sucesso!");
            return "redirect:/campanhas";
        } catch (RegraNegocioException ex) {
            model.addAttribute("mensagemErro", ex.getMessage());
            prepararFormulario(model, campanha);
            return "campanhas/formulario";
        }
    }

    @GetMapping("/{id}")
    public String exibirDetalhes(@PathVariable Long id, Model model) {
        Campanha campanha = campanhaService.buscarPorId(id);
        model.addAttribute("campanha", campanha);
        model.addAttribute("custoAcumulado", campanhaService.calcularCustoAcumulado(id));
        model.addAttribute("percentualOrcamento", campanhaService.calcularPercentualOrcamento(campanha));
        model.addAttribute("orcamentoEstourado", campanhaService.isOrcamentoEstourado(campanha));
        model.addAttribute("anuncios", anuncioService.listarPorCampanha(id));
        model.addAttribute("resumosFunil", resumoFunilRepository.listarPorCampanha(id));
        model.addAttribute("landingPages", landingPageRepository.findByCampanhaIdCampanha(id));
        model.addAttribute("statusOpcoes", List.of(Status.ATIVO, Status.PAUSADO, Status.ENCERRADO));
        return "campanhas/detalhes";
    }

    @PostMapping("/{id}/status")
    public String alterarStatus(
            @PathVariable Long id,
            @RequestParam("novoStatus") Status novoStatus,
            RedirectAttributes redirectAttributes) {
        try {
            campanhaService.alterarStatus(id, novoStatus);
            redirectAttributes.addFlashAttribute(
                    "mensagemSucesso",
                    "Status da campanha atualizado para " + novoStatus + " com sucesso!"
            );
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
        }
        return "redirect:/campanhas/" + id;
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            campanhaService.excluir(id);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Campanha excluída com sucesso!");
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
        }
        return "redirect:/campanhas";
    }

    private void prepararFormulario(Model model, Campanha campanha) {
        model.addAttribute("campanha", campanha);
        model.addAttribute("contas", contaService.listarTodas());
        model.addAttribute("statusOpcoes", List.of(Status.ATIVO, Status.PAUSADO, Status.ENCERRADO));
    }
}