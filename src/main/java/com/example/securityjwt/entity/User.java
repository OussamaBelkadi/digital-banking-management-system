package com.example.securityjwt.entity;

import com.example.securityjwt.enums.ERole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom d'utilisateur unique pour la connexion
    @Column(unique = true, nullable = false)
    private String username;

    // Email unique
    @Column(unique = true, nullable = false)
    private String email;

    // Mot de passe crypté (jamais en clair !)
    @Column(nullable = false)
    private String password;

    // Nom complet de l'employé
    @Column(name = "full_name")
    private String fullName;

    // Compte actif ou bloqué
    private boolean enabled = true;

    // Date de création du compte
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    // Dernière connexion
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Relation Many-to-Many avec les rôles
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<ERole> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }
}