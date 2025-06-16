package com.example.securityjwt.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CurrentAccountDTO {
    private Long Id;
    private double Balance;
    private LocalDateTime CreatedAt;
    private double OverDraft;
    private String Customer;
    private String Type;
}
