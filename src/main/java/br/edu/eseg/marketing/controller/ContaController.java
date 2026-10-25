package br.edu.eseg.marketing.controller;

import br.edu.eseg.marketing.domain.Conta;
import br.edu.eseg.marketing.domain.Localizacao;
import br.edu.eseg.marketing.domain.Status;
import br.edu.eseg.marketing.exception.RegraNegocioException;
import br.edu.eseg.marketing.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("contas", contaService.listarTodas());
        return "contas/lista";
    }

    @GetMapping("/nova")
    public String exibirFormularioNova(Model model) {
        Conta conta = new Conta();
        conta.setStatusConta(Status.ATIVO);
        conta.setLocalizacao(new Localizacao());
        prepararFormulario(model, conta);
        return "contas/formulario";
    }

    @GetMapping("/{id}/editar")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        Conta conta = contaService.buscarPorId(id);
        if (conta.getLocalizacao() == null) {
            conta.setLocalizacao(new Localizacao());
        }
        prepararFormulario(model, conta);
        return "contas/formulario";
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("conta") Conta conta,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            prepararFormulario(model, conta);
            return "contas/formulario";
        }

        try {
            contaService.salvar(conta);
            redirectAttributes.addFlashAttribute("mensagemSucesso", "Conta anunciante guardada com sucesso!");
            return "redirect:/contas";
        } catch (RegraNegocioException ex) {
            model.addAttribute("mensagemErro", ex.getMessage());
            prepararFormulario(model, conta);
            return "contas/formulario";
        }
    }

    private void prepararFormulario(Model model, Conta conta) {
        model.addAttribute("conta", conta);
        model.addAttribute("statusOpcoes", List.of(Status.ATIVO, Status.INATIVO));
    }
}