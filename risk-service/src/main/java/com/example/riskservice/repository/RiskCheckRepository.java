package com.example.riskservice.repository;

import com.example.riskservice.model.RiskCheck;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskCheckRepository extends MongoRepository<RiskCheck, String> {
    Optional<RiskCheck> findByTransactionId(String transactionId);
    List<RiskCheck> findByFromAccountAndRiskyTrue(String fromAccount);
}
