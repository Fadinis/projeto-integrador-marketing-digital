package br.edu.eseg.marketing.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String exibirLogin() {
        return "login";
    }

    @GetMapping("/acesso-negado")
    public String exibirAcessoNegado() {
        return "acesso-negado";
    }
}