package com.example.securityjwt.config;

import com.example.securityjwt.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Filtre qui intercepte chaque requête HTTP pour vérifier le token JWT
 * Hérite de OncePerRequestFilter pour s'exécuter une seule fois par requête
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    /**
     * Méthode principale du filtre - s'exécute pour chaque requête
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Extraction du token JWT de l'en-tête Authorization
            String jwt = parseJwt(request);

            // 2. Vérification et validation du token
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {

                // 3. Extraction du nom d'utilisateur du token
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // 4. Chargement des détails de l'utilisateur depuis la DB
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 5. Création de l'objet d'authentification Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails,
                                null,
                                userDetails.getAuthorities());

                // 6. Ajout des détails de la requête (IP, session, etc.)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 7. Définition de l'authentification dans le contexte Spring Security
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            // Log l'erreur mais ne bloque pas la requête
        }

        // 8. Continue la chaîne de filtres (très important !)
        filterChain.doFilter(request, response);
    }

    /**
     * Extrait le token JWT de l'en-tête Authorization
     * Format attendu: "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     */
    private String parseJwt(HttpServletRequest request) {

        // Récupère l'en-tête Authorization
        String headerAuth = request.getHeader("Authorization");

        // Vérifie si l'en-tête existe et commence par "Bearer "
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            // Retourne le token sans le préfixe "Bearer " (7 caractères)
            return headerAuth.substring(7);
        }

        return null;
    }
}