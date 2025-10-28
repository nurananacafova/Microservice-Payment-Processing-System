package com.example.accountservice.service;

import com.example.accountservice.dto.request.CreateUserDto;
import com.example.accountservice.dto.request.UpdateBalanceDto;
import com.example.accountservice.dto.response.UserAccountDto;
import com.example.accountservice.enums.Currency;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserAccountService {
    void createAccount(CreateUserDto dto);

    List<UserAccountDto> getAccountByUserId(Long userId);

    Map<Currency, BigDecimal> getBalancesByAccountNumber(String accountNumber);

    UserAccountDto getAccountByAccountNumber(String accountNumber);

    void updateBalance(String accountNumber, UpdateBalanceDto request);
}
