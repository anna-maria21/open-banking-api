package com.openbankingapi.controller;


import com.openbankingapi.dto.TransactionDto;
import com.openbankingapi.dto.TransactionRequestDto;
import com.openbankingapi.dto.TransactionResponseDto;
import com.openbankingapi.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/accounts/{accountId}/balance")
    public Double getAccountBalance(@PathVariable String accountId) {
        return accountService.getAccountBalance(accountId);
    }

    @GetMapping("/accounts/{accountId}/transactions")
    public List<TransactionDto> getLastTransactions(@PathVariable String accountId, Pageable pageable) {
        return accountService.getLastTransactions(accountId, pageable);
    }

    @PostMapping("/payments/initiate")
    public TransactionResponseDto initiatePayment(@RequestBody TransactionRequestDto transactionRequestDto) throws InterruptedException {
        var accountFrom = accountService.checkAccount(transactionRequestDto.ibanFrom(), transactionRequestDto.currencyCodeFrom());
        var accountTo = accountService.checkAccount(transactionRequestDto.ibanTo(), transactionRequestDto.currencyCodeTo());
        accountService.checkAccountBalance(accountFrom, transactionRequestDto.sum());
        Long transactionId = accountService.createTransaction(accountFrom, accountTo, (long) (transactionRequestDto.sum()*100));
        return accountService.initiatePayment(transactionRequestDto, transactionId);
    }
}
