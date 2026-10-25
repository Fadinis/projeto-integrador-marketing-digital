package br.edu.eseg.marketing.controller;

import br.edu.eseg.marketing.domain.Anuncio;
import br.edu.eseg.marketing.domain.Campanha;
import br.edu.eseg.marketing.domain.Status;
import br.edu.eseg.marketing.exception.RegraNegocioException;
import br.edu.eseg.marketing.service.AnuncioService;
import br.edu.eseg.marketing.service.CampanhaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/anuncios")
public class AnuncioController {

    private final AnuncioService anuncioService;
    private final CampanhaService campanhaService;

    public AnuncioController(AnuncioService anuncioService, CampanhaService campanhaService) {
        this.anuncioService = anuncioService;
        this.campanhaService = campanhaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("anuncios", anuncioService.listarTodos());
        return "anuncios/lista";
    }

    @GetMapping("/novo")
    public String exibirFormularioNovo(
            @RequestParam(value = "idCampanha", required = false) Long idCampanha,
            Model model) {
        Anuncio anuncio = new Anuncio();
        anuncio.setStatusAnuncio(Status.ATIVO);
        if (idCampanha != null) {
            Campanha campanha = campanhaService.buscarPorId(idCampanha);
            anuncio.setCampanha(campanha);
        }
        prepararFormulario(model, anuncio);
        return "anuncios/formulario";
    }

    @GetMapping("/{id}/editar")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        Anuncio anuncio = anuncioService.buscarPorId(id);
        prepararFormulario(model, anuncio);
        return "anuncios/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("anuncio") Anuncio anuncio,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            prepararFormulario(model, anuncio);
            return "anuncios/formulario";
        }

        try {
            anuncioService.salvar(anuncio);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Anúncio guardado com sucesso!");
            return "redirect:/campanhas/" + anuncio.getCampanha().getIdCampanha();
        } catch (RegraNegocioException ex) {
            model.addAttribute("mensagemErro", ex.getMessage());
            prepararFormulario(model, anuncio);
            return "anuncios/formulario";
        }
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Anuncio anuncio = anuncioService.buscarPorId(id);
        Long idCampanha = anuncio.getCampanha().getIdCampanha();
        anuncioService.excluir(id);
        redirectAttributes.addFlashAttribute("mensagemSucesso", "Anúncio excluído com sucesso!");
        return "redirect:/campanhas/" + idCampanha;
    }

    private void prepararFormulario(Model model, Anuncio anuncio) {
        model.addAttribute("anuncio", anuncio);
        model.addAttribute("campanhas", campanhaService.listarTodas());
        model.addAttribute("statusOpcoes", List.of(Status.ATIVO, Status.PAUSADO, Status.ENCERRADO));
    }
}