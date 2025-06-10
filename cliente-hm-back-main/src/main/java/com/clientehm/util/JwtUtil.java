package com.clientehm.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretBase64;

    private Key secretKey;

    @PostConstruct
    public void init() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secretBase64);
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username) {
        long expMillis = System.currentTimeMillis() + 3600000; // 1 hora
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expMillis))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateTokenAndGetUsername(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            return null;
        }
    }
}