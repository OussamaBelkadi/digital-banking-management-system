package com.example.securityjwt.controllers;

import com.example.securityjwt.dtos.CustomerDTO;
import com.example.securityjwt.exception.CustomerNotFoundException;
import com.example.securityjwt.services.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des clients
 * Toutes les méthodes nécessitent une authentification
 */
@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    @Autowired
    private BankAccountService bankAccountService;

    /**
     * RÉCUPÉRER TOUS LES CLIENTS (avec pagination)
     * GET /api/customers?page=0&size=10&sort=name
     */
    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword
    ) {
        try {
            // Configuration du tri
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ?
                    Sort.Direction.DESC : Sort.Direction.ASC;
            Sort sort = Sort.by(direction, sortBy);

            // Configuration de la pagination
            PageRequest pageRequest = PageRequest.of(page, size, sort);

            Page<CustomerDTO> customers;

            // Recherche avec ou sans mot-clé
            if (keyword != null && !keyword.trim().isEmpty()) {
                customers = bankAccountService.searchCustomers(keyword, pageRequest);
            } else {
                customers = bankAccountService.listCustomers(pageRequest);
            }

            return ResponseEntity.ok(customers);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * RÉCUPÉRER UN CLIENT PAR ID
     * GET /api/customers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        try {
            CustomerDTO customer = bankAccountService.getCustomer(id);
            return ResponseEntity.ok(customer);

        } catch (CustomerNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * CRÉER UN NOUVEAU CLIENT
     * POST /api/customers
     * Seuls les MANAGERS et ADMINS peuvent créer des clients
     */
    @PostMapping
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        try {
            CustomerDTO savedCustomer = bankAccountService.saveCustomer(customerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erreur lors de la création du client: " + e.getMessage());
        }
    }

    /**
     * MODIFIER UN CLIENT
     * PUT /api/customers/{id}
     * Seuls les MANAGERS et ADMINS peuvent modifier
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerDTO customerDTO
    ) {
        try {
            customerDTO.setId(id);  // S'assure que l'ID correspond
            CustomerDTO updatedCustomer = bankAccountService.updateCustomer(customerDTO);
            return ResponseEntity.ok(updatedCustomer);

        } catch (CustomerNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erreur lors de la modification: " + e.getMessage());
        }
    }

    /**
     * SUPPRIMER UN CLIENT
     * DELETE /api/customers/{id}
     * Seuls les ADMINS peuvent supprimer
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        try {
            bankAccountService.deleteCustomer(id);
            return ResponseEntity.ok().body("Client supprimé avec succès");

        } catch (CustomerNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    /**
     * RECHERCHER DES CLIENTS
     * GET /api/customers/search?keyword=dupont
     */
    @GetMapping("/search")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(@RequestParam String keyword) {
        try {
            List<CustomerDTO> customers = bankAccountService.searchCustomers(keyword);
            return ResponseEntity.ok(customers);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
