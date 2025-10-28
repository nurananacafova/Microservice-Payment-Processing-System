package com.example.accountservice.repository;

import com.example.accountservice.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByAccountNumber(String accountNumber);

    List<UserAccount> findByUserId(Long userId);

    boolean existsByAccountNumber(String accountNumber);
}
