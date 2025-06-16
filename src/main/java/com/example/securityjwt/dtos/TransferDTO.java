package com.example.securityjwt.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransferDTO {
    @NotBlank
    private String accountSource;
    @NotBlank
    private String accountDestination;
    @Positive
    private double amount;
    private String description;
}
