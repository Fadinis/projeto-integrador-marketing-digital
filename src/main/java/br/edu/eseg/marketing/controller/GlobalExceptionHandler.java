package br.edu.eseg.marketing.controller;

import br.edu.eseg.marketing.exception.RecursoNaoEncontradoException;
import br.edu.eseg.marketing.exception.RegraNegocioException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public String tratarRecursoNaoEncontrado(
            RecursoNaoEncontradoException ex,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
        return "redirect:/campanhas";
    }

    @ExceptionHandler(RegraNegocioException.class)
    public String tratarRegraNegocio(
            RegraNegocioException ex,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mensagemErro", ex.getMessage());
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/campanhas";
    }

    @ExceptionHandler(Exception.class)
    public String tratarErroInesperado(Exception ex, Model model) {
        model.addAttribute("mensagemErro", "Ocorreu um erro inesperado ao processar a operação: " + ex.getMessage());
        return "erro";
    }
}