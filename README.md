# Sistema de Gestão e Análise de Marketing Digital
**Projeto Integrador Full Stack — Modernização de Sistema Legado (2º Semestre de 2026)**

* **Instituição:** ESEG — Escola Superior de Engenharia e Gestão
* **Curso:** Engenharia de Computação — Programação Web
* **Estudante:** Eduardo Marques Fadini

---

## 1. Sobre o Projeto

Este projeto consiste na análise crítica, reengenharia de dados e modernização de uma base legada de Marketing Digital (`18_marketing_digital.xlsx`, composta por 31.314 registros distribuídos entre 2024 e 2025) para uma aplicação web transacional e analítica robusta.

O objetivo central é substituir controles baseados em planilhas desnormalizadas por uma arquitetura orientada a objetos e relacional íntegra (3ª Forma Normal), eliminando redundâncias, padronizando máquinas de estado (`Status`), automatizando cálculos financeiros/métricas com precisão (`BigDecimal`) e implementando controle de acesso baseado em perfis no servidor.

---

## 2. Tecnologias Utilizadas

* **Linguagem:** Java 17+
* **Framework Backend:** Spring Boot 3 (Spring Web MVC, Spring Data JPA, Validation)
* **Segurança:** Spring Security (Autenticação e Autorização baseada em perfis com BCrypt)
* **Persistência e Banco de Dados:** JPA / Hibernate + Banco de Dados Relacional SQL
* **Frontend / Template Engine:** HTML5, CSS3, Bootstrap 5 e Thymeleaf
* **Modelagem:** Astah UML & DBML / SQL DDL

---

## 3. Estrutura do Repositório

```text
├── docs/
│   ├── Relatorio_Tecnico_Entrega1.pdf   # Relatório técnico completo da 1ª Entrega
│   ├── modelo-legado.png                # Estrutura original da base legada (Antes)
│   ├── diagrama-classes-uml.png         # Diagrama de Classes UML evoluído (Fase 3)
│   ├── modelo-relacional-der.png        # Diagrama Entidade-Relacionamento SQL (Fase 4)
│   └── modelagem.asta                   # Arquivo fonte da modelagem no Astah
├── sql/
│   └── schema.sql                       # Script DDL com tabelas, PKs, FKs, CHECKs e Índices
└── README.md                            # Documentação principal do repositório
```

---

## 4. Cronograma e Status das Entregas

- [x] **1ª Entrega (23/10/2026) — Fases 1 a 4:** Diagnóstico do legado, matriz de rastreabilidade de requisitos, diagrama de classes UML, modelo relacional legado vs. proposto (DER e SQL) e justificativas técnicas.
- [ ] **2ª Entrega (06/11/2026) — Fases 5 a 8:** Projeto Spring Boot executável, banco de dados configurado, mapeamento JPA, Services, Controllers e operações CRUD persistidas.
- [ ] **3ª Entrega (13/11/2026) — Fases 9 a 10:** Autenticação e autorização com Spring Security (perfis distintos) e interface responsiva integrada com Thymeleaf e Bootstrap.
- [ ] **4ª Entrega (20/11/2026) — Fases 11 a 13:** Integração full stack completa, testes automatizados (JUnit), evidências de validação e documentação final.

---

## 5. Resumo da 1ª Entrega (Fases 1 a 4)

### 5.1. Diagnóstico Crítico da Base Legada (Fase 2)

A auditoria realizada sobre a planilha original `18_marketing_digital.xlsx` identificou 6 problemas estruturais graves:

1. **Desnormalização Severa (Violação da 2FN e 3FN):** A aba `desempenho_diario` (24.910 linhas) duplicava chaves estrangeiras transitivas (`id_conta`, `id_campanha`) e repetia 10 colunas descritivas pertencentes a `campanhas`, `anuncios` e `publicos` (`canal`, `objetivo`, `formato`, `faixa_etaria`, `genero`, `cidade`, `estado`, `regiao`). A aba `funil_mensal` também duplicava `canal`.
2. **Persistência de Colunas Calculadas e Imprecisão Numérica:** Indicadores derivados (`ctr`, `cpc`, `taxa_conversao`, `roas`, `taxa_clique`, `taxa_lead`, `taxa_venda_por_lead`) eram salvos estaticamente com erros de ponto flutuante (ex: `ctr = 0.008800000000000001`), gerando risco de inconsistência caso os dados brutos fossem atualizados.
3. **Fragmentação Temporal:** Datas decompostas manualmente em colunas inteiras (`ano`, `mes`, `trimestre`) e armazenadas como números seriais do Excel (`45292`).
4. **Inconsistência Semântica em Campos de Status:** Nomenclaturas divergentes entre abas para estados equivalentes (`Ativa` vs `Ativo`, `Encerrada` vs `Finalizado`, `Inativa`, `Arquivada`) e ausência de validação de transições.
5. **Repetição de Estruturas Geográficas:** O trio `cidade`, `estado` e `regiao` repetia-se em `contas`, `publicos` e `desempenho_diario`.
6. **Ausência de Segurança e Integridade Relacional:** Inexistência de controle de usuários/perfis, chaves estrangeiras ativas ou restrições de domínio (`CHECK` / `NOT NULL`).

---

### 5.2. Matriz de Rastreabilidade de Requisitos (Fase 1)

| ID | Requisito Original (Legado) | Problema / Lacuna | Requisito Revisado ou Novo | Justificativa | Implementação (Spring / JPA / Thymeleaf) | Teste / Evidência |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **RF01** | Registros de Contas, Campanhas e Anúncios mantidos em abas separadas sem vínculo forçado. | Falta de integridade referencial; possibilidade de criar anúncios para campanhas inexistentes. | **[Revisado]** CRUD completo de `Campanha` e `Anuncio` com integridade relacional obrigatória às entidades `Conta` e `PublicoAlvo`. | Garante consistência dos dados persistidos e navegação segura entre entidades. | Entidades JPA (`@ManyToOne`, `@OneToMany`), `Repositories`, `Controllers` MVC e formulários Thymeleaf com Bootstrap. | Teste de persistência validando criação/edição e bloqueio de exclusão de registros pais com dependências. |
| **RF02** | Campos de status em texto livre (`Ativa`, `Ativo`, `Encerrada`, `Finalizado`). | Nomenclaturas divergentes entre abas e ausência de regra de transição de estados. | **[Revisado]** Padronização via enum `Status` e validação de transições válidas no método `alterarStatus()`. | Evita estados ilegais no ciclo de vida da campanha (ex: `ENCERRADO` voltar para `ATIVO`). | Enum `Status` mapeado com `@Enumerated(EnumType.STRING)` e validação de negócio na entidade/serviço. | Teste unitário JUnit comprovando o bloqueio de transição inválida com lançamento de exceção. |
| **RF03** | Colunas fixas para `ctr`, `roas`, `taxa_conversao` e decomposição manual de datas (`ano`, `mes`, `trimestre`). | Erros de precisão decimal e inconsistência quando métricas brutas são atualizadas. | **[Revisado]** Cálculo dinâmico de indicadores em tempo de execução com `BigDecimal` e uso de `LocalDate`. | Elimina redundância de colunas calculadas (3FN) e assegura exatidão matemática. | Métodos `calcularCTR()`, `calcularROAS()`, `calcularTaxaConversao()` e `calcularTaxaAbertura()` nas classes de domínio. | Teste unitário validando o cálculo exato e o tratamento de divisão por zero quando `impressoes = 0`. |
| **RF04** | Registro de `desempenho_diario` repetindo 10 colunas de campanha, conta e público. | Redundância em 24.910 linhas e alto risco de divergência cadastral. | **[Revisado]** Normalização de `DesempenhoDiario` mantendo apenas o vínculo relacional com `Anuncio` e `PublicoAlvo`. | Aplica a 3ª Forma Normal (3FN), reduzindo o tamanho da base e garantindo fonte única da verdade. | Classe `DesempenhoDiario extends MetricasFunil` ligada a `Anuncio` e `PublicoAlvo`. | Consulta JPA com `JOIN` exibindo o relatório diário completo na interface Thymeleaf sem colunas duplicadas. |
| **RF05** | *(Inexistente no legado)* Acompanhamento de limite de orçamento da campanha. | O legado guarda `orcamento_planejado`, mas não alerta quando o custo real supera esse valor. | **[Novo]** Alerta de estouro de orçamento e bloqueio de ativação de novos anúncios quando o custo acumulado atingir o `orcamentoPlanejado`. | Agrega controle financeiro real ao processo de gestão de campanhas. | Regra na camada `Service` somando os custos da campanha + alerta visual Bootstrap no painel Thymeleaf. | Teste automatizado simulando campanha com custo acima do planejado e verificando o bloqueio. |
| **RF06** | *(Inexistente no legado)* Controle de acesso por perfil de usuário. | Qualquer pessoa podia editar dados financeiros ou apagar campanhas na planilha. | **[Novo]** Autenticação e autorização com perfis distintos (`ADMIN`, `GESTOR_MARKETING`, `ANALISTA`). | Protege operações sensíveis no servidor (`ANALISTA` apenas consulta; `GESTOR_MARKETING` gere campanhas; `ADMIN` gere contas e usuários). | Spring Security, `BCryptPasswordEncoder`, controle de rotas e anotações de segurança nos Controllers. | Teste de integração HTTP comprovando retorno `403 Forbidden` para usuário sem permissão. |

---

### 5.3. Modelagem Orientada a Objetos — Diagrama de Classes (Fase 3)

O modelo de domínio foi refatorado aplicando herança (`MetricasFunil` como `@MappedSuperclass`), composição de objetos de valor (`Localizacao` como `@Embeddable`), padronização com Enums (`Status` e `Perfil`) e métodos ricos para cálculo de indicadores e validação de estados:

![Diagrama de Classes UML](docs/diagrama-classes-uml.png)

---

### 5.4. Evolução do Modelo Relacional: Legado vs. Evoluído (Fase 4)

#### A. Estrutura Legada (Antes)
![Modelo Legado](docs/modelo-legado.png)

#### B. Modelo Relacional Evoluído — DER (Depois)
![Modelo Relacional DER](docs/modelo-relacional-der.png)

* O script SQL completo de criação do banco de dados evoluído encontra-se disponível em [`sql/schema.sql`](sql/schema.sql).
* O relatório técnico detalhado com todas as justificativas e estratégias de migração encontra-se em [`docs/Relatorio_Tecnico_Entrega1.pdf`](docs/Relatorio_Tecnico_Entrega1.pdf).

---

## 6. Como Executar o Projeto (A partir da 2ª Entrega)

1. Clone o repositório:
   ```bash
   git clone <URL_DO_SEU_REPOSITORIO>
   ```
2. Execute o script de banco de dados localizado em `sql/schema.sql` (ou utilize a configuração automática do Spring Boot / Flyway).
3. Na raiz do projeto, execute via Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Acesse a aplicação no navegador em `http://localhost:8080`.