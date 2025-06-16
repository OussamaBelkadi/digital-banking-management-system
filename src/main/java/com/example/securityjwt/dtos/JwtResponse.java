package com.example.securityjwt.dtos;

import lombok.Data;

import java.util.List;

/**
 * DTO de réponse après authentification successful
 * Contient le JWT et les informations utilisateur
 */
@Data
public class JwtResponse {

    private String token;
    private String type = "Bearer";    // Type de token
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private List<String> roles;        // Liste des rôles utilisateur

    public JwtResponse(String accessToken, Long id, String username,
                       String email, String fullName, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
    }
}