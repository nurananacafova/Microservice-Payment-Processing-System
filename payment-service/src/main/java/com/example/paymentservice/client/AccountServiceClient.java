package com.example.paymentservice.client;

import com.example.paymentservice.dto.request.UpdateBalanceRequest;
import com.example.paymentservice.enums.Currency;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@FeignClient(name = "account-service")
public interface AccountServiceClient {
    @GetMapping("/accounts/balances/{accountNumber}")
    Map<Currency, BigDecimal> getBalances(@PathVariable String accountNumber);

    @PostMapping("/accounts/balances/{accountNumber}")
    void updateBalance(@PathVariable String accountNumber, @RequestBody UpdateBalanceRequest balanceUpdate);
//    @PutMapping("/api/v1/accounts/balance/debit")
//    void debitBalance(@RequestParam("userId") String userId,
//                      @RequestParam("currency") Currency currency,
//                      @RequestParam("amount") BigDecimal amount);
//
//    // Hesap bakiyesine ekleme işlemi (Başarılı ödeme sonrası alıcıya kredi)
//    @PutMapping("/api/v1/accounts/balance/credit")
//    void creditBalance(@RequestParam("userId") String userId,
//                       @RequestParam("currency") Currency currency,
//                       @RequestParam("amount") BigDecimal amount);
}
