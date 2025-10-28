package com.example.paymentservice.service;

import com.example.paymentservice.client.AccountServiceClient;
import com.example.paymentservice.dto.request.PaymentRequestDto;
import com.example.paymentservice.dto.request.RiskCheckRequest;
import com.example.paymentservice.dto.request.UpdateBalanceRequest;
import com.example.paymentservice.dto.response.RiskCheckResponse;
import com.example.paymentservice.enums.Currency;
import com.example.paymentservice.enums.TransactionStatus;
import com.example.paymentservice.exception.InsufficientFundsException;
import com.example.paymentservice.exception.PaymentNotFoundException;
import com.example.paymentservice.exception.TransactionIdAlreadyExistsException;
import com.example.paymentservice.model.Payment;
import com.example.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AccountServiceClient accountServiceClient;

    @Value("${spring.kafka.topic.check-required}")
    String checkRiskTopic;


    @Transactional
    public Payment processPayment(PaymentRequestDto dto) {
        Optional<Payment> existing = paymentRepository.findByTransactionId(dto.getTransactionId());
        if (existing.isPresent()) {
            log.info("Payment {} already exists, returning existing", dto.getTransactionId());
            throw new TransactionIdAlreadyExistsException("Payment already exists");
        }

        Payment payment = new Payment();
        payment.setTransactionId(dto.getTransactionId());
        payment.setFromAccount(dto.getFromAccount());
        payment.setToAccount(dto.getToAccount());
        payment.setCurrency(dto.getCurrency());
        payment.setAmount(dto.getAmount());
        payment.setDescription(dto.getDescription());
        payment.setStatus(TransactionStatus.PENDING);

        if (dto.getAmount().compareTo(new BigDecimal("3000")) > 0) {
            payment.setStatus(TransactionStatus.RISK_CHECK_REQUIRED);
            payment.setRisky(true);

            RiskCheckRequest riskRequest = new RiskCheckRequest(dto.getTransactionId(), dto.getFromAccount(), dto.getAmount(), dto.getCurrency());
            kafkaTemplate.send(checkRiskTopic, riskRequest);
            paymentRepository.save(payment);
            return payment;
        } else {
            try {
                processPaymentInternal(payment);
            } catch (Exception e) {
                payment.setStatus(TransactionStatus.FAILED);
                paymentRepository.save(payment);
                throw e;
            }
            paymentRepository.save(payment);
            return payment;
        }
    }

    private void processPaymentInternal(Payment payment) {
        Map<Currency, BigDecimal> balances = accountServiceClient.getBalances(payment.getFromAccount());
        BigDecimal current = balances.get(payment.getCurrency());
        if (current == null || current.compareTo(payment.getAmount()) < 0) {
            payment.setStatus(TransactionStatus.FAILED);
            throw new InsufficientFundsException(String.format("Insufficient funds for account %s", payment.getFromAccount()));
        }
        String transactionId = payment.getTransactionId();
        accountServiceClient.updateBalance(payment.getFromAccount(),
                new UpdateBalanceRequest(payment.getCurrency(), payment.getAmount().negate(), transactionId + "-DEBIT"));

        accountServiceClient.updateBalance(payment.getToAccount(),
                new UpdateBalanceRequest(payment.getCurrency(), payment.getAmount(), transactionId + "-CREDIT"));

        payment.setStatus(TransactionStatus.COMPLETED);
        payment.setRisky(false);
    }

    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException(String.format("Payment not found: %s", transactionId)));
    }

    @KafkaListener(topics = "${spring.kafka.topic.check-approved}")
    public void handleRiskApproval(RiskCheckResponse response) {
        Payment payment = paymentRepository.findByTransactionId(response.getTransactionId())
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for risk approval"));

        if (response.isApproved()) {
            processPaymentInternal(payment);
        } else {
            payment.setStatus(TransactionStatus.FAILED);
            payment.setRisky(true);
            paymentRepository.save(payment);
        }
    }

}
