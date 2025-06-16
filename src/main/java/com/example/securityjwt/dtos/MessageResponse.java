package com.example.securityjwt.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO pour les réponses simples (succès/erreur)
 */
@Data
@AllArgsConstructor
public class MessageResponse {
    private String message;
}
