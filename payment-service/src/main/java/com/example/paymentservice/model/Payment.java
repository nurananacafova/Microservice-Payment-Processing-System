package com.example.paymentservice.model;

import com.example.paymentservice.enums.Currency;
import com.example.paymentservice.enums.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payments")
public class Payment {
    @Id
    private String id;

    private String transactionId;

    private String fromAccount;
    private String toAccount;

    private BigDecimal amount;
    private Currency currency;

    private TransactionStatus status;
    private String description;
    private boolean risky;
    private String riskCheckId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
