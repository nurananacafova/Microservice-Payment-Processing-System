package com.example.riskservice.controller;

import com.example.riskservice.model.RiskCheck;
import com.example.riskservice.service.RiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/risks")
@RequiredArgsConstructor
public class RiskController {
    private final RiskService riskService;

    @GetMapping("/check/{transactionId}")
    public ResponseEntity<RiskCheck> getRiskCheck(@PathVariable String transactionId) {
        RiskCheck riskCheck = riskService.getRiskCheckByTransactionId(transactionId);
        return new ResponseEntity<>(riskCheck, HttpStatus.OK);
    }

    @GetMapping("/risky-transactions/{fromAccount}")
    public ResponseEntity<List<RiskCheck>> getRiskyTransactions(@PathVariable String fromAccount) {
        List<RiskCheck> riskyTransactions = riskService.getRiskyTransactions(fromAccount);
        return new ResponseEntity<>(riskyTransactions, HttpStatus.OK);
    }
}
