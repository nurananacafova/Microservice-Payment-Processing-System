package com.example.paymentservice.controller;

import com.example.paymentservice.dto.request.PaymentRequestDto;
import com.example.paymentservice.dto.response.PaymentResponseDto;
import com.example.paymentservice.model.Payment;
import com.example.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/")
    public ResponseEntity<PaymentResponseDto> processPayment(@RequestBody @Valid PaymentRequestDto paymentRequest) {
        Payment payment = paymentService.processPayment(paymentRequest);

        PaymentResponseDto response = new PaymentResponseDto();
        response.setTransactionId(payment.getTransactionId());
        response.setStatus(payment.getStatus());
        response.setRisky(payment.isRisky());
        response.setMessage("Payment processed successfully");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Payment> getPayment(@PathVariable String transactionId) {
        Payment payment = paymentService.getPaymentByTransactionId(transactionId);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }
}
