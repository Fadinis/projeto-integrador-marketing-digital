package br.edu.eseg.marketing.controller;

import br.edu.eseg.marketing.domain.Campanha;
import br.edu.eseg.marketing.domain.Status;
import br.edu.eseg.marketing.repository.*;
import br.edu.eseg.marketing.service.CampanhaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    private final ContaRepository contaRepository;
    private final CampanhaService campanhaService;
    private final AnuncioRepository anuncioRepository;
    private final PublicoAlvoRepository publicoAlvoRepository;
    private final LandingPageRepository landingPageRepository;
    private final EmailMarketingRepository emailMarketingRepository;

    public DashboardController(
            ContaRepository contaRepository,
            CampanhaService campanhaService,
            AnuncioRepository anuncioRepository,
            PublicoAlvoRepository publicoAlvoRepository,
            LandingPageRepository landingPageRepository,
            EmailMarketingRepository emailMarketingRepository) {
        this.contaRepository = contaRepository;
        this.campanhaService = campanhaService;
        this.anuncioRepository = anuncioRepository;
        this.publicoAlvoRepository = publicoAlvoRepository;
        this.landingPageRepository = landingPageRepository;
        this.emailMarketingRepository = emailMarketingRepository;
    }

    @GetMapping({"/", "/dashboard"})
    public String exibirDashboard(Model model) {
        List<Campanha> todasCampanhas = campanhaService.listarTodas();

        long campanhasAtivas = todasCampanhas.stream()
                .filter(c -> c.getStatusCampanha() == Status.ATIVO)
                .count();

        long campanhasOrcamentoEstourado = todasCampanhas.stream()
                .filter(campanhaService::isOrcamentoEstourado)
                .count();

        model.addAttribute("totalContas", contaRepository.count());
        model.addAttribute("totalCampanhas", todasCampanhas.size());
        model.addAttribute("campanhasAtivas", campanhasAtivas);
        model.addAttribute("campanhasOrcamentoEstourado", campanhasOrcamentoEstourado);
        model.addAttribute("totalAnuncios", anuncioRepository.count());
        model.addAttribute("totalPublicos", publicoAlvoRepository.count());
        model.addAttribute("totalLandingPages", landingPageRepository.count());
        model.addAttribute("totalEmails", emailMarketingRepository.count());

        // Exibe as 10 campanhas mais recentes no painel principal
        model.addAttribute("campanhasRecentes", todasCampanhas.stream().limit(10).toList());
        model.addAttribute("landingPagesRecentes", landingPageRepository.listarTodasComCampanha().stream().limit(5).toList());
        model.addAttribute("emailsRecentes", emailMarketingRepository.listarTodosComConta().stream().limit(5).toList());

        return "dashboard";
    }
}