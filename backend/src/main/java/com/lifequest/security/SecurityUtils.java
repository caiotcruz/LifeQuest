package com.lifequest.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Erro no motor de segurança: Não há nenhum usuário autenticado na sessão atual.");
        }

        if (authentication.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getId();
        }

        throw new IllegalStateException("Erro de mapeamento de contexto: O principal da sessão não é do tipo UserPrincipal.");
    }
}