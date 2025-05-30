package com.clientehm.util; // Ou o pacote apropriado

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.clientehm.repository.AdministradorRepository;
import com.clientehm.entity.AdministradorEntity;
import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String path = request.getRequestURI();
        // Reduzir a verbosidade do log para caminhos públicos pode ser útil em produção, mas para debug está OK.
        logger.info("JwtRequestFilter: Processando requisição para " + path);

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.validateTokenAndGetUsername(jwt);
                logger.info("JwtRequestFilter: Token extraído e usuário (email) validado: " + username);
            } catch (IllegalArgumentException e) {
                logger.error("Não foi possível obter o token JWT: " + e.getMessage());
            } catch (ExpiredJwtException e) {
                logger.warn("Token JWT expirou: " + e.getMessage());
            } catch (Exception e) {
                logger.error("Erro ao validar token JWT: " + e.toString(), e); // Adicionado 'e' ao log
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("JwtRequestFilter: Usuário '" + username + "' encontrado no token, e não há autenticação no SecurityContextHolder.");

            // Esta é a consulta que está aparecendo repetidamente nos seus logs do Hibernate
            AdministradorEntity admin = this.administradorRepository.findByEmail(username).orElse(null);

            if (admin != null) { // admin agora é um UserDetails
                logger.info("JwtRequestFilter: Administrador '" + username + "' encontrado no banco. Configurando SecurityContext.");
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        admin, null, admin.getAuthorities()); // Usar admin.getAuthorities()

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                logger.info("JwtRequestFilter: Autenticação configurada para '" + username + "' no SecurityContextHolder.");
            } else {
                logger.warn("JwtRequestFilter: Usuário '" + username + "' do token não encontrado no banco.");
                // Se o usuário do token não existe mais, explicitamente limpar qualquer autenticação antiga
                // SecurityContextHolder.clearContext(); // Poderia ser considerado, mas o fluxo atual já não autentica
            }
        } else if (username != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            // Log para confirmar se a autenticação já existe no contexto
            logger.info("JwtRequestFilter: Autenticação para '" + username + "' já existe no SecurityContextHolder para o caminho " + path);
            // Você pode logar o tipo de autenticação:
            // logger.info("Tipo de autenticação existente: " + SecurityContextHolder.getContext().getAuthentication().getClass().getName());
            // logger.info("Principal existente: " + SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        }


        chain.doFilter(request, response);
    }
}