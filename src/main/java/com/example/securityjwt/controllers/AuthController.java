package com.example.securityjwt.controllers;



import com.example.securityjwt.config.JwtUtils;
import com.example.securityjwt.dtos.JwtResponse;
import com.example.securityjwt.dtos.LoginRequest;
import com.example.securityjwt.dtos.MessageResponse;
import com.example.securityjwt.dtos.SignupRequest;
import com.example.securityjwt.entity.Role;
import com.example.securityjwt.entity.User;
import com.example.securityjwt.enums.ERole;
import com.example.securityjwt.repositories.RoleRepository;
import com.example.securityjwt.repositories.UserRepository;
import com.example.securityjwt.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour l'authentification
 * Gère les endpoints de login et d'inscription
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    /**
     * ENDPOINT DE CONNEXION
     * POST /api/auth/signin
     */
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        try {
            // 1. Authentification avec Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword())
            );

            // 2. Définit l'authentification dans le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Génère le token JWT
            String jwt = jwtUtils.generateJwtToken(authentication);

            // 4. Récupère les détails de l'utilisateur authentifié
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            // 5. Met à jour la dernière connexion
            User user = userRepository.findByUsername(userDetails.getUsername()).orElse(null);
            if (user != null) {
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
            }

            // 6. Retourne la réponse avec le JWT et les infos utilisateur
            return ResponseEntity.ok(new JwtResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    userDetails.getEmail(),
                    userDetails.getFullName(),
                    roles
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Erreur lors de la connexion: " + e.getMessage()));
        }
    }

    /**
     * ENDPOINT D'INSCRIPTION
     * POST /api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        // 1. Vérifications de validation
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Erreur: Le nom d'utilisateur est déjà utilisé!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Erreur: L'email est déjà utilisé!"));
        }

        try {
            // 2. Création du nouvel utilisateur
            User user = new User(
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    encoder.encode(signUpRequest.getPassword()),  // Hash du mot de passe
                    signUpRequest.getFullName()
            );
            user.setCreatedDate(LocalDateTime.now());
            user.setEnabled(true);

            // 3. Attribution des rôles
            Set<String> strRoles = signUpRequest.getRole();
            Set<Role> roles = new HashSet<>();

            if (strRoles == null) {
                // Rôle par défaut : EMPLOYEE
                Role userRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                        .orElseThrow(() -> new RuntimeException("Erreur: Rôle non trouvé."));
                roles.add(userRole);
            } else {
                strRoles.forEach(role -> {
                    switch (role) {
                        case "admin":
                            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                    .orElseThrow(() -> new RuntimeException("Erreur: Rôle Admin non trouvé."));
                            roles.add(adminRole);
                            break;

                        case "manager":
                            Role managerRole = roleRepository.findByName(ERole.ROLE_MANAGER)
                                    .orElseThrow(() -> new RuntimeException("Erreur: Rôle Manager non trouvé."));
                            roles.add(managerRole);
                            break;

                        default:
                            Role userRole = roleRepository.findByName(ERole.ROLE_EMPLOYEE)
                                    .orElseThrow(() -> new RuntimeException("Erreur: Rôle Employee non trouvé."));
                            roles.add(userRole);
                    }
                });
            }

            user.setRoles(roles);

            // 4. Sauvegarde en base de données
            userRepository.save(user);

            return ResponseEntity.ok(new MessageResponse("Utilisateur enregistré avec succès!"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Erreur lors de l'inscription: " + e.getMessage()));
        }
    }

    /**
     * ENDPOINT DE DÉCONNEXION (Optionnel avec JWT)
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        // Avec JWT, la déconnexion se fait généralement côté client
        // en supprimant le token du localStorage/sessionStorage

        // Optionnel : On peut mettre le token en "blacklist"
        return ResponseEntity.ok(new MessageResponse("Déconnexion réussie!"));
    }

    /**
     * ENDPOINT DE VÉRIFICATION DU TOKEN
     * GET /api/auth/validate
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            // Supprime "Bearer " du début
            String jwt = token.substring(7);

            if (jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                // Optionnel : Récupère les infos utilisateur mises à jour
                User user = userRepository.findByUsername(username).orElse(null);

                return ResponseEntity.ok(new MessageResponse("Token valide"));
            } else {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Token invalide"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Erreur de validation: " + e.getMessage()));
        }
    }
}
