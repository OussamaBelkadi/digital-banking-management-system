package com.example.securityjwt.services;

import com.example.securityjwt.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service qui implémente UserDetailsService de Spring Security
 * Charge les détails de l'utilisateur depuis la base de données
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    /**
     * Méthode obligatoire de UserDetailsService
     * Appelée automatiquement par Spring Security lors de l'authentification
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Recherche l'utilisateur dans la base de données
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));

        // Convertit l'entité User en UserDetails (format Spring Security)
        return UserDetailsImpl.build(user);
    }
}