package com.example.riskservice.model;

import com.example.riskservice.enums.Currency;
import com.example.riskservice.enums.RiskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "risk_checks")
public class RiskCheck {

    @Id
    private String id;
    private String transactionId;
    private String fromAccount;
    private BigDecimal amount;
    private Currency currency;
    private RiskStatus status;
    private String reason;
    private boolean risky;
    @CreatedDate
    private LocalDateTime createdAt;
}
