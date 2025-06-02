package com.openbankingapi.controller;


import com.openbankingapi.dto.TransactionDto;
import com.openbankingapi.dto.TransactionRequestDto;
import com.openbankingapi.dto.TransactionResponseDto;
import com.openbankingapi.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccountController {

    private final AccountService accountService;


    @Operation(summary = "Get account balance by IBAN",
            description = "Returns a double value - account's balance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found, balance returned in double format"),
            @ApiResponse(responseCode = "404", description = "Account not found by provided IBAN")
    })
    @GetMapping("/accounts/{accountId}/balance")
    public Double getAccountBalance(@Parameter(description = "IBAN of the account to get balance")
                                    @PathVariable String accountId) {
        return accountService.getAccountBalance(accountId);
    }

    @Operation(summary = "Get transactions by IBAN in descending order",
            description = "Returns a list of transactions for account fetched by provided IBAN in descending order " +
                    "ordered by timestamp. Supports pagination. Example: ?page=0&size=5")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found, list of transactions returned"),
            @ApiResponse(responseCode = "404", description = "Account not found by provided IBAN")
    })
    @GetMapping("/accounts/{accountId}/transactions")
    public List<TransactionDto> getLastTransactions(@Parameter(description = "IBAN of the account to get transactions")
                                                    @PathVariable String accountId, @ParameterObject Pageable pageable) {
        return accountService.getLastTransactions(accountId, pageable);
    }

    @Operation(summary = "Payment initiation",
            description = "Returns a response from an external payment API. Checks the account existing in the db and " +
                    "balance is enough for current operation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Both accounts found, payment successfully sent to an API"),
            @ApiResponse(responseCode = "404", description = "Any account not found by provided IBANs and currencies"),
            @ApiResponse(responseCode = "400", description = "Not enough money on debit account")
    })
    @PostMapping("/payments/initiate")
    public TransactionResponseDto initiatePayment(@Parameter(description = "IBAN & currency of both debit and credit " +
            "accounts and payment sum in double format")
                                                  @RequestBody TransactionRequestDto transactionRequestDto) {
        var accountFrom = accountService.checkAccount(transactionRequestDto.ibanFrom(), transactionRequestDto.currencyCodeFrom());
        var accountTo = accountService.checkAccount(transactionRequestDto.ibanTo(), transactionRequestDto.currencyCodeTo());
        accountService.checkAccountBalance(accountFrom, transactionRequestDto.sum());
        Long transactionId = accountService.createTransaction(accountFrom, accountTo, (long) (transactionRequestDto.sum() * 100));
        return accountService.initiatePayment(transactionRequestDto, transactionId);
    }
}
