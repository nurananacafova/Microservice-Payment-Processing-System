package com.example.riskservice.service;

import com.example.riskservice.exception.RiskCheckNotFoundException;
import com.example.riskservice.repository.RiskCheckRepository;
import com.example.riskservice.dto.RiskCheckRequest;
import com.example.riskservice.dto.RiskCheckResponse;
import com.example.riskservice.enums.RiskStatus;
import com.example.riskservice.model.RiskCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RiskService {
    private final RiskCheckRepository riskCheckRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.check-approved}")
    String checkApprovedTopic;

    @KafkaListener(topics = "${spring.kafka.topic.check-required}")
    public void handleRiskCheck(RiskCheckRequest request) {
        log.info("Processing risk check for transaction: {}", request.getTransactionId());
        RiskCheck rc = new RiskCheck();
        rc.setTransactionId(request.getTransactionId());
        rc.setFromAccount(request.getFromAccount());
        rc.setAmount(request.getAmount());
        rc.setCurrency(request.getCurrency());
        rc.setStatus(RiskStatus.PENDING);

        boolean isRisky = checkRiskRules(request);
        rc.setRisky(isRisky);
        if (isRisky) {
            rc.setStatus(RiskStatus.REJECTED);
            rc.setReason("Amount exceeds risk threshold");
        } else {
            rc.setStatus(RiskStatus.APPROVED);
            rc.setReason("Approved by risk system");
        }
        riskCheckRepository.save(rc);

        RiskCheckResponse res = new RiskCheckResponse();
        res.setTransactionId(request.getTransactionId());
        res.setApproved(!isRisky);
        res.setMessage(rc.getReason());
        kafkaTemplate.send(checkApprovedTopic, res);
        log.info("Risk check completed for {}: risky={}", request.getTransactionId(), isRisky);
    }

    private boolean checkRiskRules(RiskCheckRequest req) {
        if (req.getAmount().compareTo(new BigDecimal("3000")) > 0) {
            return true;
        }
        return false;
    }

    public RiskCheck getRiskCheckByTransactionId(String transactionId) {
        return riskCheckRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RiskCheckNotFoundException("Risk check not found: " + transactionId));
    }

    public List<RiskCheck> getRiskyTransactions(String fromAccount) {
        return riskCheckRepository.findByFromAccountAndRiskyTrue(fromAccount);
    }
}
