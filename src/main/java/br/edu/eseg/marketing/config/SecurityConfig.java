package br.edu.eseg.marketing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Recursos estaticos, login e H2 Console
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/h2-console/**").permitAll()

                        // Regras exclusivas de ADMIN (Gerir Contas e Excluir Campanhas/Anuncios)
                        .requestMatchers("/contas/nova", "/contas/*/editar", "/contas/salvar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/campanhas/*/excluir", "/anuncios/*/excluir").hasRole("ADMIN")

                        // Regras de Escrita para ADMIN e GESTOR_MARKETING (Criar/Editar Campanhas, Anuncios e Status)
                        .requestMatchers("/campanhas/nova", "/campanhas/*/editar", "/campanhas/salvar", "/campanhas/*/status")
                        .hasAnyRole("ADMIN", "GESTOR_MARKETING")
                        .requestMatchers("/anuncios/novo", "/anuncios/*/editar", "/anuncios/salvar")
                        .hasAnyRole("ADMIN", "GESTOR_MARKETING")

                        // Regras de Leitura para todos os perfis autenticados (ADMIN, GESTOR_MARKETING, ANALISTA)
                        .requestMatchers(HttpMethod.GET, "/", "/dashboard", "/campanhas/**", "/anuncios/**", "/contas/**")
                        .hasAnyRole("ADMIN", "GESTOR_MARKETING", "ANALISTA")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .failureUrl("/login?erro=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/acesso-negado")
                )
                // Permite que o painel /h2-console funcione com frames
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }
}