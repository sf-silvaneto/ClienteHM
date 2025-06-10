package com.clientehm.util;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// import org.slf4j.Logger; // Não é mais necessário se todos os logs forem removidos
// import org.slf4j.LoggerFactory; // Não é mais necessário
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.clientehm.repository.AdministradorRepository;
import com.clientehm.entity.AdministradorEntity;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.validateTokenAndGetUsername(jwt);
            } catch (IllegalArgumentException e) {
            } catch (ExpiredJwtException e) {
            } catch (Exception e) {
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            AdministradorEntity admin = this.administradorRepository.findByEmail(username).orElse(null);

            if (admin != null) {
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        admin, null, admin.getAuthorities());

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                } else {
                }
        } else if (username != null && SecurityContextHolder.getContext().getAuthentication() != null) {
            }

        chain.doFilter(request, response);
    }
}