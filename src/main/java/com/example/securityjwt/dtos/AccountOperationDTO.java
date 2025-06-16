package com.example.securityjwt.dtos;

import com.example.securityjwt.enums.OperationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AccountOperationDTO {
    private Long id;
    private LocalDateTime operationDate;
    private double amount;
    private String description;
    private OperationType type;
    private String performedBy;
}