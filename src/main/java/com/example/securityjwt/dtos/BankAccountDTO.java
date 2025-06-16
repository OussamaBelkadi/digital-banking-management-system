package com.example.securityjwt.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BankAccountDTO {
    private String id;
    private double balance;
    private LocalDateTime createdAt;
    private String type;
    private CustomerDTO customer;
    private double overDraft; // pour CurrentAccount
    private double interestRate;
}
