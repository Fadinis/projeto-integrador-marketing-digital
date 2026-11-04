package br.edu.eseg.marketing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SegurancaIntegracaoTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("RF06: Usuário não autenticado deve ser redirecionado para /login ao tentar acessar o dashboard")
    void deveRedirecionarParaLoginQuandoNaoAutenticado() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("RF06: Perfil ANALISTA tem permissão de leitura GET para listagem de campanhas")
    @WithMockUser(username = "analista@eseg.edu.br", roles = {"ANALISTA"})
    void analistaDeveConseguirVisualizarCampanhas() throws Exception {
        mockMvc.perform(get("/campanhas"))
                .andExpect(status().isOk())
                .andExpect(view().name("campanhas/lista"));
    }

    @Test
    @DisplayName("RF06: Perfil ANALISTA é bloqueado com 403 Forbidden ao tentar excluir campanha (POST)")
    @WithMockUser(username = "analista@eseg.edu.br", roles = {"ANALISTA"})
    void analistaNaoPodeExcluirCampanha() throws Exception {
        mockMvc.perform(post("/campanhas/1/excluir").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("RF06: Perfil GESTOR_MARKETING não tem permissão para criar contas de clientes (403 Forbidden)")
    @WithMockUser(username = "gestor@eseg.edu.br", roles = {"GESTOR_MARKETING"})
    void gestorNaoPodeAcessarCadastroContas() throws Exception {
        mockMvc.perform(get("/contas/nova"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("RF06: Perfil ADMIN possui acesso autorizado ao cadastro de novas contas de clientes")
    @WithMockUser(username = "admin@eseg.edu.br", roles = {"ADMIN"})
    void adminPodeAcessarCadastroContas() throws Exception {
        mockMvc.perform(get("/contas/nova"))
                .andExpect(status().isOk())
                .andExpect(view().name("contas/formulario"));
    }
}
