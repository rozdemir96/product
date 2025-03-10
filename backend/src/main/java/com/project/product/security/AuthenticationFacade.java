package com.project.product.security;

import com.project.product.entity.User;
import com.project.product.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacade {
    private final UserRepository userRepository;

    public AuthenticationFacade(@Lazy UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Geçerli oturumdaki kullanıcıyı döndürür.
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("User not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser(); // ✅ Kullanıcıyı al
        }

        throw new RuntimeException("Invalid authentication principal: " + principal.getClass().getName());
    }

    /**
     * Geçerli oturumdaki kullanıcının ID'sini döndürür.
     */
    public Long getCurrentUserId() {
        try {
            return getCurrentUser().getId();
        } catch (AccessDeniedException | EntityNotFoundException e) {
            return null;
        }
    }
}
