package com.example.accountservice.dto.response;

import com.example.accountservice.enums.Currency;
import com.example.accountservice.model.UserAccount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BalanceDto {
    private Long id;
    private Currency currency;
    private BigDecimal amount;
    private UserAccount userAccount;
}
