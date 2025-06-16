package com.example.securityjwt.dtos;

import com.example.securityjwt.entity.Customer;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class SavingAccountDTO {
    private Long Id;
    private double Balance;
    private LocalDateTime CreatedAt;
    private double InterestRate;
    private Customer Customer;
    private String Type;
}
