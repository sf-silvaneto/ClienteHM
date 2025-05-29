package com.clientehm.util; // ou com.clientehm.config

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Adicionado para especificar o método HTTP
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy; // Adicionado para API stateless
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // Habilita a configuração de segurança web do Spring
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Desabilitar CSRF, comum para APIs stateless (como a sua, que usa JWT)
                .csrf(csrf -> csrf.disable())
                // Configurar a política de criação de sessão para STATELESS, pois usaremos JWT
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Permitir acesso público (sem autenticação) aos endpoints de login e registro
                        .requestMatchers(HttpMethod.POST, "/api/administradores/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/administradores/registrar").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/administradores/verificar-palavra-chave").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/administradores/redefinir-senha").permitAll()

                        // Qualquer outra requisição precisa estar autenticada
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}