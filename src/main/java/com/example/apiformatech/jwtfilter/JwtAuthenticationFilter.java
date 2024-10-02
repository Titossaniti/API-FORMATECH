package com.example.apiformatech.jwtfilter;

import com.example.apiformatech.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Récupérer le header "Authorization" de la requête
        String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String username = null;

        // Vérifie si l'en-tête contient le token JWT
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7); // Extrait le token sans "Bearer "
            username = jwtService.extractUsername(jwtToken); // Extrait l'utilisateur du token
        }

        // Si le token est valide et que l'utilisateur n'est pas déjà authentifié
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Si le token est valide, configure le contexte de sécurité pour cet utilisateur
            if (jwtService.validateToken(jwtToken, userDetails)) {
                var authentication = jwtService.getAuthenticationToken(userDetails, request);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continuer avec la chaîne de filtres
        chain.doFilter(request, response);
    }
}
