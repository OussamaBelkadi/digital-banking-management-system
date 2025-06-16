package com.example.securityjwt.entity;

import com.example.securityjwt.enums.ERole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entité représentant un rôle dans le système
 * Ex: ADMIN, EMPLOYEE, MANAGER
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue
    private Long id;

    // Nom du rôle (ADMIN, EMPLOYEE, etc.)
    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private ERole name;

    // Description du rôle
    private String description;

    public Role(ERole name) {
        this.name = name;
    }
}