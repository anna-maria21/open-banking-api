package com.openbankingapi.service;

import com.openbankingapi.dto.TransactionDto;
import com.openbankingapi.dto.TransactionRequestDto;
import com.openbankingapi.dto.TransactionResponseDto;
import com.openbankingapi.exception.InvalidSumException;
import com.openbankingapi.exception.NoSuchAccountException;
import com.openbankingapi.exception.NoSuchAccountForTransactionException;
import com.openbankingapi.exception.NoSuchCurrencyException;
import com.openbankingapi.mapper.TransactionConverter;
import com.openbankingapi.properties.AppConfigVariables;
import com.openbankingapi.repository.AccountRepository;
import com.openbankingapi.repository.CurrencyRepository;
import com.openbankingapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CurrencyRepository currencyRepository;
    private final TransactionConverter transactionConverter;
    private final RestTemplate restTemplate;
    private final AppConfigVariables appConfigVariables;

    public Double getAccountBalance(String accountId) {
        log.info("Getting account balance for account {}", accountId);

        var account = accountRepository.getAccountByIban(accountId)
                .orElseThrow(() -> new NoSuchAccountException(accountId));
        return account.getBalance() / 100.0;
    }

    public List<TransactionDto> getLastTransactions(String accountId, Pageable pageable) {
        log.info("Getting last transactions for account {}", accountId);
        var account = accountRepository.getAccountByIban(accountId)
                .orElseThrow(() -> new NoSuchAccountException(accountId));

        return transactionRepository.findAllByAccount(accountId, pageable).stream()
                .map(transactionConverter::transactionToDto)
                .collect(Collectors.toList());
    }

    public TransactionResponseDto initiatePayment(TransactionRequestDto transactionRequest) {
        log.info("Initiating payment for account {} ({}) to account {}, sum: {}",
                transactionRequest.ibanFrom(),
                transactionRequest.currencyCodeFrom(),
                transactionRequest.ibanTo(),
                transactionRequest.sum());
        validateTransactionParameters(transactionRequest);

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var transactionRequestHttpEntity = new HttpEntity<>(transactionRequest, headers);
        return restTemplate.postForObject(appConfigVariables.getUrl(), transactionRequestHttpEntity, TransactionResponseDto.class);
    }

    private void validateTransactionParameters(TransactionRequestDto transactionRequest) {
        var accountFrom = accountRepository.getAccountByIban(transactionRequest.ibanFrom());
        var accountTo = accountRepository.getAccountByIban(transactionRequest.ibanTo());

        if (accountFrom.isEmpty() && accountTo.isEmpty()) {
            throw new NoSuchAccountForTransactionException(transactionRequest.ibanFrom(), transactionRequest.ibanTo());
        }

        var currencyFrom = currencyRepository.findByCode(transactionRequest.currencyCodeFrom()).orElseThrow(() -> new NoSuchCurrencyException(transactionRequest.currencyCodeFrom()));

        if (transactionRequest.sum() <= 0) {
            throw new InvalidSumException(transactionRequest.sum());
        }
    }
}
