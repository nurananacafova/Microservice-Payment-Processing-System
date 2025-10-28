package com.example.riskservice.service;

import com.example.riskservice.dto.RiskCheckRequest;
import com.example.riskservice.dto.RiskCheckResponse;
import com.example.riskservice.model.RiskCheck;
import com.example.riskservice.repository.RiskCheckRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RiskServiceTest {
    @Mock
    private RiskCheckRepository riskCheckRepository;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private RiskService riskService;

    @BeforeEach
    void setUp() {
        riskService = new RiskService(riskCheckRepository, kafkaTemplate);
        riskService.checkApprovedTopic = "check-approved";
    }

    @Test
    void handleRiskCheckApproved() {
        RiskCheckRequest req = new RiskCheckRequest();
        req.setTransactionId("TransactionId1");
        req.setFromAccount("Account1");
        req.setAmount(BigDecimal.valueOf(100));

        when(riskCheckRepository.save(any(RiskCheck.class))).thenAnswer(i -> i.getArguments()[0]);

        riskService.handleRiskCheck(req);

        verify(riskCheckRepository, times(1)).save(any(RiskCheck.class));
        verify(kafkaTemplate, times(1)).send(eq("check-approved"), any(RiskCheckResponse.class));
    }

    @Test
    void handleRiskCheckRejected() {
        RiskCheckRequest req = new RiskCheckRequest();
        req.setTransactionId("TransactionId1");
        req.setFromAccount("Account1");
        req.setAmount(BigDecimal.valueOf(4000));

        when(riskCheckRepository.save(any(RiskCheck.class))).thenAnswer(i -> i.getArguments()[0]);

        riskService.handleRiskCheck(req);

        verify(riskCheckRepository, times(1)).save(any(RiskCheck.class));
        verify(kafkaTemplate, times(1)).send(eq("check-approved"), any(RiskCheckResponse.class));
    }
}