package com.example.accountservice.dto.request;

import com.example.accountservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalanceDto {
    private Currency currency;
    private BigDecimal amount;
    private String transactionId;
}
