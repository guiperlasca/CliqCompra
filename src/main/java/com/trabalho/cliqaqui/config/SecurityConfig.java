package com.trabalho.cliqaqui.config;

import com.trabalho.cliqaqui.security.JpaUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Habilita a segurança web do Spring Security [cite: 2]
@EnableMethodSecurity(prePostEnabled = true) // Permite usar @PreAuthorize nos métodos [cite: 2]
public class SecurityConfig {

    @Autowired
    private JpaUserDetailsService userDetailsService;

    // Define o codificador de senhas (BCrypt recomendado)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura o provedor de autenticação para usar seu UserDetailsService e o codificador de senhas
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Expose o AuthenticationManager como um bean para que ele possa ser injetado (no AuthController, por exemplo)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Define a cadeia de filtros de segurança HTTP
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desabilita CSRF para APIs REST [cite: 2]
                .cors(cors -> cors.and()) // Habilita CORS configurado globalmente (no CliqConfig) [cite: 2]
                .authorizeHttpRequests(authorize -> authorize
                        // Permite acesso público aos endpoints de autenticação, ao console H2, e a recursos estáticos
                        .requestMatchers("/auth/**", "/h2-console/**", "/login.html", "/css/**", "/js/**", "/images/**").permitAll() // <-- ADICIONADO AQUI
                        .anyRequest().authenticated() // Todas as outras requisições exigem autenticação [cite: 2]
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Sem estado, bom para APIs REST com JWT ou sessões gerenciadas pelo cliente [cite: 2]
                );

        // Configuração para permitir o console do H2 Frame (apenas em desenvolvimento) [cite: 2]
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));

        return http.build();
    }
}