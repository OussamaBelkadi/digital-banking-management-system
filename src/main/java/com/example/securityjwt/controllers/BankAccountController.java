package com.example.securityjwt.controllers;

import com.example.securityjwt.dtos.BankAccountDTO;
import com.example.securityjwt.dtos.CurrentBankAccountDTO;
import com.example.securityjwt.exception.BankAccountNotFoundException;
import com.example.securityjwt.exception.CustomerNotFoundException;
import com.example.securityjwt.services.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des comptes bancaires
 */
@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class BankAccountController {

    @Autowired
    private BankAccountService bankAccountService;

    /**
     * RÉCUPÉRER TOUS LES COMPTES
     * GET /api/accounts
     */
    @GetMapping
    public ResponseEntity<List<BankAccountDTO>> getAllAccounts() {
        try {
            List<BankAccountDTO> accounts = bankAccountService.bankAccountList();
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * RÉCUPÉRER UN COMPTE PAR ID
     * GET /api/accounts/{accountId}
     */
    @GetMapping("/{accountId}")
    public ResponseEntity<BankAccountDTO> getAccount(@PathVariable String accountId) {
        try {
            BankAccountDTO account = bankAccountService.getBankAccount(accountId);
            return ResponseEntity.ok(account);
        } catch (BankAccountNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * RÉCUPÉRER LES COMPTES D'UN CLIENT
     * GET /api/accounts/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<BankAccountDTO>> getCustomerAccounts(@PathVariable Long customerId) {
        try {
            List<BankAccountDTO> accounts = bankAccountService.getAccountsByCustomerId(customerId);
            return ResponseEntity.ok(accounts);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * CRÉER UN COMPTE COURANT
     * POST /api/accounts/current
     * Seuls MANAGERS et ADMINS peuvent créer des comptes
     */
    @PostMapping("/current")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> createCurrentAccount(@Valid @RequestBody CurrentBankAccountDTO accountDTO) {
        try {
            CurrentBankAccountDTO savedAccount = bankAccountService.saveCurrentBankAccount(
                    accountDTO.getBalance(),
                    accountDTO.getCustomerId(),
                    accountDTO.getOverDraft()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAccount);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.badRequest().body("Client non trouvé");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erreur lors de la création du compte: " + e.getMessage());
        }
    }

    /**
     * CRÉER UN COMPTE ÉPARGNE
     * POST /api/accounts/saving
     */
    @PostMapping("/saving")
    @PreAuthorize("hasRole('MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<?> createSavingAccount(@Valid @RequestBody SavingBankAccountDTO accountDTO) {
        try {
            SavingBankAccountDTO savedAccount = bankAccountService.saveSavingBankAccount(
                    accountDTO.getBalance(),
                    accountDTO.getCustomerId(),
                    accountDTO.getInterestRate()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAccount);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.badRequest().body("Client non trouvé");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Erreur lors de la création du compte: " + e.getMessage());
        }
    }
}
