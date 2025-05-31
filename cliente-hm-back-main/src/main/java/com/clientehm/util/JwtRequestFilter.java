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
// Removido import java.util.ArrayList; se não estiver sendo usado para UserDetails authorities

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
        // O log original era INFO, o que é bom para produção. DEBUG é mais verboso.
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
                logger.error("Erro ao validar token JWT: " + e.toString(), e);
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("JwtRequestFilter: Usuário '" + username + "' encontrado no token, e não há autenticação no SecurityContextHolder.");

            AdministradorEntity admin = this.administradorRepository.findByEmail(username).orElse(null);

            if (admin != null) { // admin agora é um UserDetails
                logger.info("JwtRequestFilter: Administrador '" + username + "' encontrado no banco. Configurando SecurityContext.");

                // Declaração ÚNICA da variável aqui
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        admin, null, admin.getAuthorities()); // Usar admin.getAuthorities()

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                logger.info("JwtRequestFilter: Autenticação configurada para '" + username + "' no SecurityContextHolder. Autenticação é: " + SecurityContextHolder.getContext().getAuthentication());
            } else {
                logger.warn("JwtRequestFilter: Usuário '" + username + "' do token não encontrado no banco.");
            }
        } else if (username != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            logger.info("JwtRequestFilter: Autenticação para '" + username + "' já existe no SecurityContextHolder para o caminho " + path);
        }

        chain.doFilter(request, response);
    }
}