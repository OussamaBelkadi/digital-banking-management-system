package com.example.securityjwt.exception;

/**
 * Exception lancée quand un client n'est pas trouvé
 */
public class CustomerNotFoundException extends RuntimeException {

    // Constructeur avec message simple
    public CustomerNotFoundException(String message) {
        super(message);
    }

    // Constructeur avec message et cause
    public CustomerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}