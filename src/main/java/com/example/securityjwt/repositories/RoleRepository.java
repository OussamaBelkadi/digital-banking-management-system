package com.example.securityjwt.repositories;

import com.example.securityjwt.entity.Role;
import com.example.securityjwt.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Trouve un rôle par son nom
     */
    Optional<Role> findByName(ERole name);
}