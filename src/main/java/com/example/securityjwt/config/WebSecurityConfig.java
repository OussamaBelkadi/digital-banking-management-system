package com.example.securityjwt.config;

import com.example.securityjwt.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuration principale de Spring Security
 * Définit les règles d'authentification et d'autorisation
 */
@Configuration
@EnableWebSecurity  // Active Spring Security
@EnableMethodSecurity(prePostEnabled = true)  // Active les annotations @PreAuthorize
public class WebSecurityConfig {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    /**
     * Crée le filtre JWT personnalisé
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Fournisseur d'authentification personnalisé
     * Lie UserDetailsService et PasswordEncoder
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);  // Notre service pour charger les users
        authProvider.setPasswordEncoder(passwordEncoder());      // BCrypt pour hasher les mots de passe

        return authProvider;
    }

    /**
     * Configuration du gestionnaire d'authentification
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig)
            throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Encodeur de mot de passe - BCrypt (recommandé)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();    // Hash avec salt automatique
    }

    /**
     * Configuration CORS pour permettre les requêtes depuis le frontend
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origines autorisées (Frontend Angular)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        // configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));  // Plus sécurisé

        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // En-têtes autorisés
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Autorise l'envoi des credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * CONFIGURATION PRINCIPALE DU FILTRE DE SÉCURITÉ
     * Définit les règles d'accès pour chaque URL
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))  // Active CORS
                .csrf(csrf -> csrf.disable())  // Désactive CSRF (pas nécessaire avec JWT)

                // Gestion des erreurs d'authentification
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(unauthorizedHandler))

                // Configuration des sessions - STATELESS pour JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // RÈGLES D'AUTORISATION DES URLS
                .authorizeHttpRequests(authz -> authz

                        // ===== ROUTES PUBLIQUES (pas d'authentification requise) =====
                        .requestMatchers("/api/auth/**").permitAll()          // Login, register
                        .requestMatchers("/api/public/**").permitAll()        // Infos publiques
                        .requestMatchers("/h2-console/**").permitAll()        // Console H2 (dev only)
                        .requestMatchers("/swagger-ui/**").permitAll()        // Documentation API
                        .requestMatchers("/v3/api-docs/**").permitAll()       // OpenAPI docs

                        // ===== ROUTES AVEC RÔLES SPÉCIFIQUES =====
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")               // Admin seulement
                        .requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER")  // Admin ou Manager

                        // ===== ROUTES PROTÉGÉES (authentification requise) =====
                        .requestMatchers("/api/customers/**").authenticated()   // Gestion clients
                        .requestMatchers("/api/accounts/**").authenticated()    // Gestion comptes
                        .requestMatchers("/api/operations/**").authenticated()  // Opérations bancaires

                        // ===== TOUTES LES AUTRES ROUTES =====
                        .anyRequest().authenticated()    // Toute autre route nécessite une authentification
                );

        // Configuration du provider d'authentification
        http.authenticationProvider(authenticationProvider());

        // Ajout du filtre JWT AVANT le filtre d'authentification standard
        http.addFilterBefore(authenticationJwtTokenFilter(),
                UsernamePasswordAuthenticationFilter.class);

        // Configuration pour H2 Console (développement uniquement)
        http.headers(headers -> headers.frameOptions().sameOrigin());

        return http.build();
    }
}
