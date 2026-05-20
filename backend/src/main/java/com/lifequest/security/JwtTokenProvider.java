package com.lifequest.security;

import com.lifequest.config.LifeQuestProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final LifeQuestProperties appProperties;

    private SecretKey getSigningKey() {
        byte[] keyBytes = appProperties.getJwt().getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ── Gerar token ────────────────────────────────────────────

    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return buildToken(userDetails.getUsername(), appProperties.getJwt().getExpirationMs());
    }

    public String generateTokenFromUsername(String username) {
        return buildToken(username, appProperties.getJwt().getExpirationMs());
    }

    public String generateRefreshToken(String username) {
        return buildToken(username, appProperties.getJwt().getRefreshExpirationMs());
    }

    private String buildToken(String subject, long expirationMs) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
            .subject(subject)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(getSigningKey())
            .compact();
    }

    // ── Extrair dados ──────────────────────────────────────────

    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public Date getExpirationFromToken(String token) {
        return parseClaims(token).getExpiration();
    }

    // ── Validar token ──────────────────────────────────────────

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT expirado: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT malformado: {}", e.getMessage());
        } catch (SecurityException e) {
            log.warn("Assinatura JWT inválida: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT string vazia: {}", e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}