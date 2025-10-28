package com.example.accountservice.controller;

import com.example.accountservice.dto.request.CreateUserDto;
import com.example.accountservice.dto.request.UpdateBalanceDto;
import com.example.accountservice.dto.response.UserAccountDto;
import com.example.accountservice.enums.Currency;
import com.example.accountservice.service.UserAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accounts")
public class UserAccountController {
    private final UserAccountService accountService;


    @PostMapping("/")
    public ResponseEntity<String> createAccount(@RequestBody @Valid CreateUserDto dto) {
        accountService.createAccount(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserAccountDto>> getAccountByUserId(@PathVariable Long userId) {
        List<UserAccountDto> userAccount = accountService.getAccountByUserId(userId);
        return new ResponseEntity<>(userAccount, HttpStatus.OK);
    }

    @GetMapping("/balances/{accountNumber}")
    public ResponseEntity<Map<Currency, BigDecimal>> getBalancesByAccountNumber(@PathVariable String accountNumber) {
        Map<Currency, BigDecimal> balance = accountService.getBalancesByAccountNumber(accountNumber);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<UserAccountDto> getAccountByAccountNumber(@PathVariable String accountNumber) {
        UserAccountDto account = accountService.getAccountByAccountNumber(accountNumber);
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PostMapping("/balances/{accountNumber}")
    public ResponseEntity<String> updateBalance(@PathVariable String accountNumber,
                                                @RequestBody @Valid UpdateBalanceDto req) {
        accountService.updateBalance(accountNumber, req);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
