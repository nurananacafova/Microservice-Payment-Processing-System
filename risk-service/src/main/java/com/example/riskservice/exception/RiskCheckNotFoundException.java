package com.example.riskservice.exception;

public class RiskCheckNotFoundException extends RuntimeException {
    public RiskCheckNotFoundException(String message) {
        super(message);
    }
}
