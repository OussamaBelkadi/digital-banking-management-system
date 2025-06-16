package com.example.securityjwt.config;

import lombok.Value;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.logging.Logger;

/**
 * Utilitaire pour générer, valider et parser les tokens JWT
 */
@Component
public class JwtUtils {

    // Logger pour tracer les erreurs JWT
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    // Clé secrète depuis application.yml
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    // Durée d'expiration du token (24h par défaut)
    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    /**
     * Génère un token JWT à partir de l'authentification
     */
    public String generateJwtToken(Authentication authentication) {

        // Récupère les détails de l'utilisateur authentifié
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))  // Nom d'utilisateur comme subject
                .setIssuedAt(new Date())                     // Date de création
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))  // Date d'expiration
                .signWith(key(), SignatureAlgorithm.HS256)   // Signature avec clé secrète
                .compact();                                  // Génère le token final
    }

    /**
     * Génère la clé de signature à partir du secret
     */
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Extrait le nom d'utilisateur du token JWT
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())           // Utilise la même clé pour vérifier
                .build()
                .parseClaimsJws(token)          // Parse et vérifie le token
                .getBody()                      // Récupère le payload
                .getSubject();                  // Extrait le subject (username)
    }

    /**
     * Valide un token JWT
     * Vérifie la signature, l'expiration, etc.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            // Tente de parser le token
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parse(authToken);
            return true;

        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}