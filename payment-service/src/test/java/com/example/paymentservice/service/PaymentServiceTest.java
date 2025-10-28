package com.example.paymentservice.service;

import com.example.paymentservice.client.AccountServiceClient;
import com.example.paymentservice.dto.request.PaymentRequestDto;
import com.example.paymentservice.dto.request.UpdateBalanceRequest;
import com.example.paymentservice.enums.Currency;
import com.example.paymentservice.enums.TransactionStatus;
import com.example.paymentservice.exception.InsufficientFundsException;
import com.example.paymentservice.exception.TransactionIdAlreadyExistsException;
import com.example.paymentservice.model.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, kafkaTemplate, accountServiceClient);
        paymentService.checkRiskTopic = "check-risk";
    }

    @Test
    void processPaymentSuccess() {
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setTransactionId("TransactionId1");
        dto.setFromAccount("Account1");
        dto.setToAccount("Account2");
        dto.setCurrency(Currency.USD);
        dto.setAmount(BigDecimal.valueOf(100));

        when(paymentRepository.findByTransactionId("TransactionId1")).thenReturn(Optional.empty());
        when(accountServiceClient.getBalances("Account1")).thenReturn(Map.of(Currency.USD, BigDecimal.valueOf(200)));

        Payment result = paymentService.processPayment(dto);

        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        verify(accountServiceClient).updateBalance(eq("Account1"), any(UpdateBalanceRequest.class));
        verify(accountServiceClient).updateBalance(eq("Account2"), any(UpdateBalanceRequest.class));
    }

    @Test
    void processPayment_InsufficientFunds() {
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setTransactionId("TransactionId1");
        dto.setFromAccount("Account1");
        dto.setToAccount("Account2");
        dto.setCurrency(Currency.USD);
        dto.setAmount(BigDecimal.valueOf(300));

        when(paymentRepository.findByTransactionId("TransactionId1")).thenReturn(Optional.empty());
        when(accountServiceClient.getBalances("Account1")).thenReturn(Map.of(Currency.USD, BigDecimal.valueOf(200)));

        assertThrows(InsufficientFundsException.class, () -> paymentService.processPayment(dto));
    }

    @Test
    void processPayment_AlreadyExists() {
        PaymentRequestDto dto = new PaymentRequestDto();
        dto.setTransactionId("TransactionId1");
        when(paymentRepository.findByTransactionId("TransactionId1")).thenReturn(Optional.of(new Payment()));

        assertThrows(TransactionIdAlreadyExistsException.class, () -> paymentService.processPayment(dto));
    }
}