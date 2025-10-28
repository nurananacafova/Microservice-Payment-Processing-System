package com.example.accountservice.service.impl;

import com.example.accountservice.Mapper.ModelMapper;
import com.example.accountservice.dto.request.CreateUserDto;
import com.example.accountservice.dto.request.UpdateBalanceDto;
import com.example.accountservice.dto.response.UserAccountDto;
import com.example.accountservice.enums.Currency;
import com.example.accountservice.exception.*;
import com.example.accountservice.model.Balance;
import com.example.accountservice.model.TransactionRecord;
import com.example.accountservice.model.UserAccount;
import com.example.accountservice.repository.BalanceRepository;
import com.example.accountservice.repository.TransactionRecordRepository;
import com.example.accountservice.repository.UserAccountRepository;
import com.example.accountservice.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountRepository accountRepository;
    private final BalanceRepository balanceRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TransactionRecordRepository transactionRecordRepository;
    private final ModelMapper modelMapper;

    @Override
    public void createAccount(CreateUserDto dto) {
        if (accountRepository.existsByAccountNumber(dto.getAccountNumber())) {
            throw new AccountAlreadyExistsException("Account already exists: " + dto.getAccountNumber());
        }
        UserAccount account = new UserAccount();
        account.setAccountNumber(dto.getAccountNumber());
        account.setUserId(dto.getUserId());

        List<Balance> balances = new ArrayList<>();
        if (dto.getBalances() != null) {
            for (Map.Entry<Currency, BigDecimal> e : dto.getBalances().entrySet()) {
                Balance b = new Balance();
                b.setCurrency(e.getKey());
                b.setAmount(e.getValue() == null ? BigDecimal.ZERO : e.getValue());
                b.setUserAccount(account);
                balances.add(b);
            }
        }
        account.setBalances(balances);
        UserAccount saved = accountRepository.save(account);

        kafkaTemplate.send("account-created", modelMapper.toUserAccountDto(saved));
    }

    @Override
    public List<UserAccountDto> getAccountByUserId(Long userId) {
        List<UserAccount> userAccount = accountRepository.findByUserId(userId);
        if (userAccount.isEmpty()) throw new AccountNotFoundException("Account not found for user: " + userId);
        return modelMapper.toUserAccountDtoList(userAccount);
    }

    @Override
    public UserAccountDto getAccountByAccountNumber(String accountNumber) {
        UserAccount userAccount = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        return modelMapper.toUserAccountDto(userAccount);
    }

    @Override
    public Map<Currency, BigDecimal> getBalancesByAccountNumber(String accountNumber) {
        UserAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        List<Balance> balance = balanceRepository.findByUserAccount(account);
        return balance.stream().collect(Collectors.toMap(Balance::getCurrency, Balance::getAmount));
    }

    @Override
    @Transactional
    public void updateBalance(String accountNumber, UpdateBalanceDto request) {
        if (request.getTransactionId() == null || request.getTransactionId().isBlank()) {
            throw new IllegalArgumentException("transactionId is required for idempotency");
        }
        if (transactionRecordRepository.existsByTransactionId(request.getTransactionId())) {
            log.info("Transaction {} already applied. Ignoring duplicate.", request.getTransactionId());
            return;
        }

        UserAccount account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));

        Balance balance = balanceRepository.findByUserAccountAndCurrency(account, request.getCurrency())
                .orElseThrow(() -> new BalanceNotFoundException("Balance not found for currency: " + request.getCurrency().name()));

        BigDecimal newBal = balance.getAmount().add(request.getAmount());
        if (newBal.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException("Insufficient funds for currency: " + request.getCurrency());
        }

        balance.setAmount(newBal);
        balanceRepository.save(balance);

        TransactionRecord rec = new TransactionRecord();
        rec.setTransactionId(request.getTransactionId());
        rec.setAccountNumber(accountNumber);
        rec.setCurrency(request.getCurrency());
        rec.setAmount(request.getAmount());
        transactionRecordRepository.save(rec);

        Map<String, Object> event = new HashMap<>();
        event.put("accountNumber", accountNumber);
        event.put("currency", request.getCurrency());
        event.put("amount", newBal);
        event.put("transactionId", request.getTransactionId());
        kafkaTemplate.send("balance-updated", event);

        log.info("Balance updated for {} {} -> new: {}", accountNumber, request.getCurrency(), newBal);
    }
}
