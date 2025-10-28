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
    public ResponseEntity<String> processPayment(@RequestBody @Valid PaymentRequestDto paymentRequest) {
        paymentService.processPayment(paymentRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Payment> getPayment(@PathVariable String transactionId) {
        Payment payment = paymentService.getPaymentByTransactionId(transactionId);
        return new ResponseEntity<>(payment, HttpStatus.OK);
    }
}
