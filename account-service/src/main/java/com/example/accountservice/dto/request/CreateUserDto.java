package com.example.accountservice.dto.request;

import com.example.accountservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDto {
    private Long userId;
    private String accountNumber;
    private Map<Currency, BigDecimal> balances;
}
