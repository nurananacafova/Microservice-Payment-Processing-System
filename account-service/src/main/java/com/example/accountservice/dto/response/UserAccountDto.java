package com.example.accountservice.dto.response;

import com.example.accountservice.model.Balance;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserAccountDto {
    private Long id;
    private String accountNumber;
    private Long userId;
    private List<Balance> balances;
    private LocalDateTime createdAt;
}
