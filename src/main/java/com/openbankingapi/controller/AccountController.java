package com.openbankingapi.controller;


import com.openbankingapi.dto.TransactionDto;
import com.openbankingapi.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
