package com.example.riskservice.dto;

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
