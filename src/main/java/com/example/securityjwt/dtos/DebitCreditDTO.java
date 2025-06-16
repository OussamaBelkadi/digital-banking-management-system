package com.example.securityjwt.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DebitCreditDTO {
    @NotBlank
    private String accountId;
    @Positive
    private double amount;
    private String description;
}
