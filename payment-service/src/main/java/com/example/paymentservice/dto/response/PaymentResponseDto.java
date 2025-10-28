package com.example.paymentservice.dto.response;

import com.example.paymentservice.enums.TransactionStatus;
import lombok.Data;

@Data
public class PaymentResponseDto {
    private String transactionId;
    private TransactionStatus status;
    private boolean risky;
    private String message;
}
