package com.example.securityjwt.repositories;

import com.example.securityjwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son nom d'utilisateur
     * Utilisé pour l'authentification
     */
    Optional<User> findByUsername(String username);

    /**
     * Trouve un utilisateur par email
     * Utile pour la récupération de mot de passe
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un nom d'utilisateur existe déjà
     * Évite les doublons lors de l'inscription
     */
    Boolean existsByUsername(String username);

    /**
     * Vérifie si un email existe déjà
     */
    Boolean existsByEmail(String email);

    /**
     * Requête personnalisée pour récupérer un utilisateur avec ses rôles
     */
    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(String username);
}
