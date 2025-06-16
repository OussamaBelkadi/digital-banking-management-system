package com.example.securityjwt.services;

import com.example.securityjwt.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implémentation de UserDetails qui adapte notre entité User
 * aux besoins de Spring Security
 */
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String email;
    private String fullName;

    // @JsonIgnore empêche la sérialisation du mot de passe
    @JsonIgnore
    private String password;

    // Collection des autorités/rôles de l'utilisateur
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * Constructeur privé - utiliser la méthode build() pour créer une instance
     */
    public UserDetailsImpl(Long id, String username, String email, String fullName,
                           String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.authorities = authorities;
    }

    /**
     * Méthode factory qui convertit une entité User en UserDetailsImpl
     * Pattern Builder pour une création propre
     */
    public static UserDetailsImpl build(User user) {

        // Convertit les rôles de l'utilisateur en GrantedAuthority
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());

        return new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPassword(),
                authorities);
    }

    // ===== Méthodes obligatoires de UserDetails =====

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // ===== États du compte =====

    /**
     * Le compte n'est pas expiré
     * Retourne toujours true - peut être personnalisé selon les besoins
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Le compte n'est pas verrouillé
     * Peut être lié à un champ 'locked' dans l'entité User
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Les credentials ne sont pas expirés
     * Peut être lié à une date d'expiration du mot de passe
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Le compte est activé
     * Peut être lié au champ 'enabled' de l'entité User
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    // ===== Méthodes utilitaires =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    // Getters pour accéder aux propriétés supplémentaires
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
}