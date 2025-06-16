package com.example.securityjwt.entity;

import com.example.securityjwt.enums.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountOperation {
    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime operationTime;
    private double amount;
    private String description;
    @Enumerated(EnumType.STRING)
    private OperationType operationType;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private BankAccount bankAccount;
    private String performedBy;

    @PrePersist
    public void prePersist() {
        operationTime = LocalDateTime.now();
    }

}
