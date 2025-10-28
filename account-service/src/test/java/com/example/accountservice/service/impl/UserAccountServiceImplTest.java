package com.example.accountservice.service.impl;

import com.example.accountservice.Mapper.ModelMapper;
import com.example.accountservice.dto.request.CreateUserDto;
import com.example.accountservice.dto.request.UpdateBalanceDto;
import com.example.accountservice.enums.Currency;
import com.example.accountservice.exception.AccountAlreadyExistsException;
import com.example.accountservice.exception.AccountNotFoundException;
import com.example.accountservice.model.Balance;
import com.example.accountservice.model.UserAccount;
import com.example.accountservice.repository.BalanceRepository;
import com.example.accountservice.repository.TransactionRecordRepository;
import com.example.accountservice.repository.UserAccountRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserAccountServiceImplTest {
    @Mock
    private UserAccountRepository accountRepository;
    @Mock
    private BalanceRepository balanceRepository;
    @Mock
    private TransactionRecordRepository transactionRecordRepository;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private UserAccountServiceImpl userAccountService;

    @BeforeEach
    void setUp() {
        userAccountService = new UserAccountServiceImpl(accountRepository, balanceRepository, kafkaTemplate, transactionRecordRepository, modelMapper);
        userAccountService.balanceUpdateTopic = "balance-updates";
        userAccountService.accountCreatedTopic = "account-created";
    }

    @Test
    void createAccountSuccess() {
        CreateUserDto dto = new CreateUserDto();
        dto.setAccountNumber("ACC123");
        dto.setUserId(1L);
        dto.setBalances(Map.of(Currency.USD, BigDecimal.valueOf(100)));

        when(accountRepository.existsByAccountNumber("ACC123")).thenReturn(false);
        when(accountRepository.save(any(UserAccount.class))).thenAnswer(i -> i.getArguments()[0]);

        userAccountService.createAccount(dto);

        verify(accountRepository, times(1)).save(any(UserAccount.class));
        verify(kafkaTemplate, times(1)).send(eq("account-created"), any());
    }

    @Test
    void createAccountAlreadyExists() {
        when(accountRepository.existsByAccountNumber("ACC123")).thenReturn(true);

        CreateUserDto dto = new CreateUserDto();
        dto.setAccountNumber("ACC123");

        assertThrows(AccountAlreadyExistsException.class, () -> userAccountService.createAccount(dto));
    }

    @Test
    void getAccountByAccountNumberNotFound() {
        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class,
                () -> userAccountService.getAccountByAccountNumber("ACC123"));
    }

    @Test
    void updateBalanceSuccess() {
        UpdateBalanceDto dto = new UpdateBalanceDto();
        dto.setCurrency(Currency.USD);
        dto.setAmount(BigDecimal.valueOf(50));
        dto.setTransactionId("TransactionId1");

        UserAccount account = new UserAccount();
        account.setAccountNumber("ACC123");
        Balance balance = new Balance();
        balance.setAmount(BigDecimal.valueOf(100));
        balance.setCurrency(Currency.USD);
        balance.setUserAccount(account);

        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
        when(balanceRepository.findByUserAccountAndCurrency(account, Currency.USD))
                .thenReturn(Optional.of(balance));
        when(transactionRecordRepository.existsByTransactionIdAndAccountNumber("TransactionId1", "ACC123"))
                .thenReturn(false);
        when(balanceRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        when(transactionRecordRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        userAccountService.updateBalance("ACC123", dto);

        assertEquals(BigDecimal.valueOf(150), balance.getAmount());
        verify(kafkaTemplate, times(1)).send(eq("balance-updates"), any());
    }

    @Test
    void updateBalanceInsufficientFunds() {
        UpdateBalanceDto dto = new UpdateBalanceDto();
        dto.setCurrency(Currency.USD);
        dto.setAmount(BigDecimal.valueOf(-200));
        dto.setTransactionId("TransactionId1");

        UserAccount account = new UserAccount();
        account.setAccountNumber("ACC123");
        Balance balance = new Balance();
        balance.setAmount(BigDecimal.valueOf(100));
        balance.setCurrency(Currency.USD);
        balance.setUserAccount(account);

        when(accountRepository.findByAccountNumber("ACC123")).thenReturn(Optional.of(account));
        when(balanceRepository.findByUserAccountAndCurrency(account, Currency.USD))
                .thenReturn(Optional.of(balance));
        when(transactionRecordRepository.existsByTransactionIdAndAccountNumber("TransactionId1", "ACC123"))
                .thenReturn(false);

        assertThrows(Exception.class, () -> userAccountService.updateBalance("ACC123", dto));
    }
}