package br.edu.eseg.marketing.config;

import br.edu.eseg.marketing.domain.*;
import br.edu.eseg.marketing.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    private static final LocalDate EXCEL_EPOCH = LocalDate.of(1899, 12, 30);

    @Bean
    public CommandLineRunner carregarDadosDoExcel(
            UsuarioRepository usuarioRepo,
            ContaRepository contaRepo,
            CampanhaRepository campanhaRepo,
            AnuncioRepository anuncioRepo,
            PublicoAlvoRepository publicoRepo,
            LandingPageRepository lpRepo,
            EmailMarketingRepository emailRepo,
            ResumoFunilRepository resumoRepo,
            DesempenhoDiarioRepository desempenhoRepo) {

        return args -> {
            // Se ja importou as 40 contas do Excel, nao importa novamente
            if (contaRepo.count() >= 40) {
                log.info(">>> Base de dados ja contem os dados migrados ({} contas). Pulando importacao.", contaRepo.count());
                return;
            }

            // Limpa eventuais dados de teste anteriores para importar o Excel limpo
            desempenhoRepo.deleteAllInBatch();
            resumoRepo.deleteAllInBatch();
            emailRepo.deleteAllInBatch();
            lpRepo.deleteAllInBatch();
            anuncioRepo.deleteAllInBatch();
            campanhaRepo.deleteAllInBatch();
            publicoRepo.deleteAllInBatch();
            contaRepo.deleteAllInBatch();
            usuarioRepo.deleteAllInBatch();

            // 1. Cria os 3 perfis de utilizadores para o Spring Security (Requisito 2.7)
            criarUsuariosPadrao(usuarioRepo);

            ClassPathResource resource = new ClassPathResource("18_marketing_digital.xlsx");
            if (!resource.exists()) {
                log.warn(">>> ATENCAO: Ficheiro '18_marketing_digital.xlsx' nao encontrado em src/main/resources/!");
                return;
            }

            log.info(">>> Iniciando migracao e higienizacao da base legada '18_marketing_digital.xlsx'...");
            Map<String, List<List<String>>> planilhas = lerPlanilhasXlsx(resource.getInputStream());

            // Mapas em memoria para vincular rapidamente as chaves estrangeiras (FKs)
            Map<Long, Conta> mapaContas = new HashMap<>();
            Map<Long, Campanha> mapaCampanhas = new HashMap<>();
            Map<Long, Anuncio> mapaAnuncios = new HashMap<>();
            Map<Long, PublicoAlvo> mapaPublicos = new HashMap<>();

            // 2. Importar Contas (sheet1.xml -> 40 registos)
            List<List<String>> linhasContas = planilhas.getOrDefault("xl/worksheets/sheet1.xml", List.of());
            for (int i = 1; i < linhasContas.size(); i++) {
                List<String> r = linhasContas.get(i);
                Long idLegado = Long.parseLong(r.get(0));
                Conta c = new Conta();
                c.setNomeConta(r.get(1));
                c.setSegmento(r.get(2));
                c.setProdutoPrincipal(r.get(3));
                c.setLocalizacao(new Localizacao(r.get(4), r.get(5), r.get(6)));
                c.setPorteEmpresa(r.get(7));
                c.setAnoInicioCliente(Integer.parseInt(r.get(8)));
                c.setStatusConta(converterStatus(r.get(9)));
                mapaContas.put(idLegado, contaRepo.save(c));
            }

            // 3. Importar Campanhas (sheet2.xml -> 160 registos)
            List<List<String>> linhasCampanhas = planilhas.getOrDefault("xl/worksheets/sheet2.xml", List.of());
            for (int i = 1; i < linhasCampanhas.size(); i++) {
                List<String> r = linhasCampanhas.get(i);
                Long idLegado = Long.parseLong(r.get(0));
                Long idConta = Long.parseLong(r.get(1));
                Campanha camp = new Campanha();
                camp.setConta(mapaContas.get(idConta));
                camp.setNomeCampanha(r.get(2));
                camp.setCanal(r.get(3));
                camp.setObjetivo(r.get(4));
                camp.setSegmento(r.get(5));
                camp.setProduto(r.get(6));
                camp.setDataInicio(converterDataSerialExcel(r.get(7)));
                camp.setDataFim(converterDataSerialExcel(r.get(8)));
                camp.setOrcamentoPlanejado(converterDecimal(r.get(9)));
                camp.setStatusCampanha(converterStatus(r.get(10)));
                camp.setPosicaoFunil(r.get(11));
                camp.setAbrangenciaGeografica(r.get(12));
                mapaCampanhas.put(idLegado, campanhaRepo.save(camp));
            }

            // 4. Importar Anuncios (sheet3.xml -> 560 registos)
            List<List<String>> linhasAnuncios = planilhas.getOrDefault("xl/worksheets/sheet3.xml", List.of());
            for (int i = 1; i < linhasAnuncios.size(); i++) {
                List<String> r = linhasAnuncios.get(i);
                Long idLegado = Long.parseLong(r.get(0));
                Long idCampanha = Long.parseLong(r.get(1));
                Anuncio an = new Anuncio();
                an.setCampanha(mapaCampanhas.get(idCampanha));
                an.setNomeAnuncio(r.get(2));
                an.setFormato(r.get(3));
                an.setVersaoTesteAb(r.get(4));
                an.setLinhaCriativa(r.get(5));
                an.setStatusAnuncio(converterStatus(r.get(6)));
                an.setNotaQualidadeCriativo(converterDecimal(r.get(7)));
                mapaAnuncios.put(idLegado, anuncioRepo.save(an));
            }

            // 5. Importar Publicos-Alvo (sheet4.xml -> 220 registos)
            List<List<String>> linhasPublicos = planilhas.getOrDefault("xl/worksheets/sheet4.xml", List.of());
            for (int i = 1; i < linhasPublicos.size(); i++) {
                List<String> r = linhasPublicos.get(i);
                Long idLegado = Long.parseLong(r.get(0));
                PublicoAlvo pub = new PublicoAlvo();
                pub.setNomePublico(r.get(1));
                pub.setFaixaEtaria(r.get(2));
                pub.setGenero(r.get(3));
                pub.setLocalizacao(new Localizacao(r.get(4), r.get(5), r.get(6)));
                pub.setTipoSegmentacao(r.get(7));
                pub.setIntencaoCompra(r.get(8));
                pub.setTamanhoEstimadoPublico(Long.parseLong(r.get(9)));
                mapaPublicos.put(idLegado, publicoRepo.save(pub));
            }

            // 6. Importar Landing Pages (sheet8.xml -> 120 registos)
            List<List<String>> linhasLp = planilhas.getOrDefault("xl/worksheets/sheet8.xml", List.of());
            List<LandingPage> listaLp = new ArrayList<>();
            for (int i = 1; i < linhasLp.size(); i++) {
                List<String> r = linhasLp.get(i);
                LandingPage lp = new LandingPage();
                lp.setCampanha(mapaCampanhas.get(Long.parseLong(r.get(1))));
                lp.setNomeLandingPage(r.get(2));
                lp.setProduto(r.get(3));
                lp.setTipoPagina(r.get(4));
                lp.setVisitas(Long.parseLong(r.get(5)));
                lp.setConversoes(Long.parseLong(r.get(6)));
                // Ignora r.get(7) (taxa_conversao calculada persistida no legado)
                lp.setTempoMedioPaginaMin(converterDecimal(r.get(8)));
                lp.setStatusLandingPage(converterStatus(r.get(9)));
                listaLp.add(lp);
            }
            lpRepo.saveAll(listaLp);

            // 7. Importar Email Marketing (sheet7.xml -> 1.473 registos)
            List<List<String>> linhasEmail = planilhas.getOrDefault("xl/worksheets/sheet7.xml", List.of());
            List<EmailMarketing> listaEmail = new ArrayList<>();
            for (int i = 1; i < linhasEmail.size(); i++) {
                List<String> r = linhasEmail.get(i);
                EmailMarketing em = new EmailMarketing();
                em.setData(converterDataSerialExcel(r.get(1)));
                // Ignora r.get(2) e r.get(3) (ano e mes redundantes)
                em.setConta(mapaContas.get(Long.parseLong(r.get(4))));
                em.setTipoEmail(r.get(5));
                em.setEnvios(Long.parseLong(r.get(6)));
                em.setAberturas(Long.parseLong(r.get(7)));
                em.setCliques(Long.parseLong(r.get(8)));
                em.setConversoes(Long.parseLong(r.get(9)));
                em.setDescadastros(Long.parseLong(r.get(10)));
                em.setCustoEnvio(converterDecimal(r.get(11)));
                em.setReceitaAtribuida(converterDecimal(r.get(12)));
                listaEmail.add(em);
            }
            emailRepo.saveAll(listaEmail);

            // 8. Importar Funil Mensal -> ResumoFunil (sheet6.xml -> 3.831 registos)
            List<List<String>> linhasFunil = planilhas.getOrDefault("xl/worksheets/sheet6.xml", List.of());
            List<ResumoFunil> listaFunil = new ArrayList<>();
            for (int i = 1; i < linhasFunil.size(); i++) {
                List<String> r = linhasFunil.get(i);
                int ano = Integer.parseInt(r.get(1));
                int mes = Integer.parseInt(r.get(2));
                ResumoFunil rf = new ResumoFunil();
                rf.setData(LocalDate.of(ano, mes, 1));
                rf.setCampanha(mapaCampanhas.get(Long.parseLong(r.get(4))));
                // Ignora r.get(5) (canal duplicado) e taxas calculadas (13, 14, 15)
                rf.setImpressoes(Long.parseLong(r.get(6)));
                rf.setCliques(Long.parseLong(r.get(7)));
                rf.setLeads(Long.parseLong(r.get(8)));
                rf.setOportunidades(Long.parseLong(r.get(9)));
                rf.setVendas(Long.parseLong(r.get(10)));
                rf.setCusto(converterDecimal(r.get(11)));
                rf.setReceita(converterDecimal(r.get(12)));
                listaFunil.add(rf);
            }
            resumoRepo.saveAll(listaFunil);

            // 9. Importar Desempenho Diario normalizado na 3FN (sheet5.xml -> 24.910 registos)
            List<List<String>> linhasDesempenho = planilhas.getOrDefault("xl/worksheets/sheet5.xml", List.of());
            List<DesempenhoDiario> loteDesempenho = new ArrayList<>(2000);
            int totalDesempenho = 0;
            for (int i = 1; i < linhasDesempenho.size(); i++) {
                List<String> r = linhasDesempenho.get(i);
                DesempenhoDiario dd = new DesempenhoDiario();
                dd.setData(converterDataSerialExcel(r.get(1)));
                // Ignora colunas redundantes de data (2..4), conta/campanha (5..6) e atributos repetidos (9..16)
                dd.setAnuncio(mapaAnuncios.get(Long.parseLong(r.get(7))));
                dd.setPublicoAlvo(mapaPublicos.get(Long.parseLong(r.get(8))));
                dd.setDispositivo(r.get(17));
                dd.setImpressoes(Long.parseLong(r.get(18)));
                dd.setCliques(Long.parseLong(r.get(19)));
                dd.setLeads(Long.parseLong(r.get(20)));
                dd.setOportunidades(Long.parseLong(r.get(21)));
                dd.setVendas(Long.parseLong(r.get(22)));
                dd.setCusto(converterDecimal(r.get(23)));
                dd.setReceita(converterDecimal(r.get(24)));
                // Ignora colunas calculadas ctr, cpc, taxa_conversao, roas (25..28)

                loteDesempenho.add(dd);
                totalDesempenho++;
                if (loteDesempenho.size() >= 2000) {
                    desempenhoRepo.saveAll(loteDesempenho);
                    loteDesempenho.clear();
                }
            }
            if (!loteDesempenho.isEmpty()) {
                desempenhoRepo.saveAll(loteDesempenho);
            }

            log.info(">>> MIGRACAO CONCLUIDA COM SUCESSO! Registos importados do Excel:");
            log.info("    - Contas: {}", mapaContas.size());
            log.info("    - Campanhas: {}", mapaCampanhas.size());
            log.info("    - Anuncios: {}", mapaAnuncios.size());
            log.info("    - Publicos-Alvo: {}", mapaPublicos.size());
            log.info("    - Landing Pages: {}", listaLp.size());
            log.info("    - E-mail Marketing: {}", listaEmail.size());
            log.info("    - Resumos de Funil: {}", listaFunil.size());
            log.info("    - Desempenho Diario (3FN): {}", totalDesempenho);
        };
    }

    private void criarUsuariosPadrao(UsuarioRepository usuarioRepo) {
        Usuario admin = new Usuario();
        admin.setNome("Administrador ESEG");
        admin.setEmail("admin@eseg.edu.br");
        admin.setSenhaHash("{noop}admin123");
        admin.setPerfil(Perfil.ADMIN);
        usuarioRepo.save(admin);

        Usuario gestor = new Usuario();
        gestor.setNome("Gestor de Marketing");
        gestor.setEmail("gestor@eseg.edu.br");
        gestor.setSenhaHash("{noop}gestor123");
        gestor.setPerfil(Perfil.GESTOR_MARKETING);
        usuarioRepo.save(gestor);

        Usuario analista = new Usuario();
        analista.setNome("Analista de Dados");
        analista.setEmail("analista@eseg.edu.br");
        analista.setSenhaHash("{noop}analista123");
        analista.setPerfil(Perfil.ANALISTA);
        usuarioRepo.save(analista);
    }

    // Padroniza os status divergentes da planilha legada (Requisito 2.3)
    private Status converterStatus(String statusLegado) {
        if (statusLegado == null) return Status.ATIVO;
        return switch (statusLegado.trim().toLowerCase()) {
            case "ativa", "ativo" -> Status.ATIVO;
            case "pausada", "pausado" -> Status.PAUSADO;
            case "encerrada", "finalizado", "encerrado" -> Status.ENCERRADO;
            case "inativa", "inativo" -> Status.INATIVO;
            case "arquivada", "arquivado" -> Status.ARQUIVADO;
            default -> Status.ATIVO;
        };
    }

    // Converte numero serial de data do Excel (ex: 45292 -> 2024-01-01)
    private LocalDate converterDataSerialExcel(String serial) {
        long dias = Long.parseLong(serial.trim());
        return EXCEL_EPOCH.plusDays(dias);
    }

    // Converte valores monetarios/taxas corrigindo dizimas de ponto flutuante do legado
    private BigDecimal converterDecimal(String valor) {
        return new BigDecimal(valor.trim()).setScale(2, RoundingMode.HALF_UP);
    }

    // Leitor nativo StAX para extrair as abas XML de dentro do ficheiro .xlsx
    private Map<String, List<List<String>>> lerPlanilhasXlsx(InputStream inputStream) throws Exception {
        Map<String, List<List<String>>> resultado = new HashMap<>();
        XMLInputFactory factory = XMLInputFactory.newInstance();

        try (ZipInputStream zis = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String nome = entry.getName();
                if (nome.startsWith("xl/worksheets/sheet") && nome.endsWith(".xml")) {
                    byte[] bytes = zis.readAllBytes();
                    XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(bytes));
                    List<List<String>> linhas = new ArrayList<>();
                    List<String> linhaAtual = null;
                    String valorCelula = "";

                    while (reader.hasNext()) {
                        int evento = reader.next();
                        if (evento == XMLStreamConstants.START_ELEMENT) {
                            String tag = reader.getLocalName();
                            if ("row".equals(tag)) {
                                linhaAtual = new ArrayList<>();
                            } else if ("c".equals(tag)) {
                                valorCelula = "";
                            } else if ("v".equals(tag) || "t".equals(tag)) {
                                valorCelula = reader.getElementText();
                            }
                        } else if (evento == XMLStreamConstants.END_ELEMENT) {
                            String tag = reader.getLocalName();
                            if ("c".equals(tag) && linhaAtual != null) {
                                linhaAtual.add(valorCelula.trim());
                            } else if ("row".equals(tag) && linhaAtual != null) {
                                linhas.add(linhaAtual);
                            }
                        }
                    }
                    reader.close();
                    resultado.put(nome, linhas);
                }
                zis.closeEntry();
            }
        }
        return resultado;
    }
}