package com.example.paymentservice.dto.request;

import com.example.paymentservice.enums.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskCheckRequest {
    private String transactionId;
    private String fromAccount;
    private BigDecimal amount;
    private Currency currency;
}
