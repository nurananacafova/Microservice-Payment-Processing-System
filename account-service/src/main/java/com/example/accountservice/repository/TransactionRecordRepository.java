package com.example.accountservice.repository;

import com.example.accountservice.model.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRecordRepository extends JpaRepository<TransactionRecord, Long> {
    boolean existsByTransactionId(String transactionId);
    boolean existsByTransactionIdAndAccountNumber(String transactionId, String accountNumber);
}
