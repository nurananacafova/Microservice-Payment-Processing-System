package com.example.paymentservice.dto.request;

import com.example.paymentservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBalanceRequest {
    private Currency currency;
    private BigDecimal amount;
    private String transactionId;
}
