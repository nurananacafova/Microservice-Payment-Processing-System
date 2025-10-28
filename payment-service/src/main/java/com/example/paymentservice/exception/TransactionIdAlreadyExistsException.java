package com.example.paymentservice.exception;

public class TransactionIdAlreadyExistsException extends RuntimeException {
    public TransactionIdAlreadyExistsException(String message) {
        super(message);
    }
}
