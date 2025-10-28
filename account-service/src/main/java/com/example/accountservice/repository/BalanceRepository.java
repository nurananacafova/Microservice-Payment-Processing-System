package com.example.accountservice.repository;

import com.example.accountservice.enums.Currency;
import com.example.accountservice.model.Balance;
import com.example.accountservice.model.UserAccount;
import feign.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Balance b where b.userAccount = :account and b.currency = :currency")
    Optional<Balance> findByUserAccountAndCurrency(@Param("account") UserAccount account, @Param("currency") Currency currency);

    List<Balance> findByUserAccount(UserAccount account);
}
