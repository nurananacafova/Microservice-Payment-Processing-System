package com.example.paymentservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiskCheckResponse {
    private String transactionId;
    private boolean approved;
    private String message;
}
