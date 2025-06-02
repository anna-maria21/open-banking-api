package com.openbankingapi.service;

import com.openbankingapi.dto.TransactionDto;
import com.openbankingapi.dto.TransactionRequestDto;
import com.openbankingapi.dto.TransactionResponseDto;
import com.openbankingapi.entity.Account;
import com.openbankingapi.entity.Status;
import com.openbankingapi.entity.Transaction;
import com.openbankingapi.exception.NoSuchAccountException;
import com.openbankingapi.exception.NoSuchTransactionException;
import com.openbankingapi.exception.NotEnoughMoneyOnBalanceException;
import com.openbankingapi.mapper.TransactionConverter;
import com.openbankingapi.properties.AppConfigVariables;
import com.openbankingapi.repository.AccountRepository;
import com.openbankingapi.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionConverter transactionConverter;
    private final RestTemplate restTemplate;
    private final AppConfigVariables appConfigVariables;


    @Transactional(readOnly = true)
    public Double getAccountBalance(String accountId) {
        log.info("Getting account balance for account {}", accountId);

        var account = accountRepository.getAccountByIban(accountId)
                .orElseThrow(() -> new NoSuchAccountException(accountId));
        return account.getBalance() / 100.0;
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getLastTransactions(String accountId, Pageable pageable) {
        log.info("Getting last transactions for account {}", accountId);
        var account = accountRepository.getAccountByIban(accountId)
                .orElseThrow(() -> new NoSuchAccountException(accountId));

        return transactionRepository.findAllByAccount(accountId, pageable).stream()
                .map(transactionConverter::transactionToDto)
                .collect(Collectors.toList());
    }

    public TransactionResponseDto initiatePayment(TransactionRequestDto transactionRequest,
                                                  Long transactionId) throws InterruptedException {
        log.info("Initiating payment for account {} ({}) to account {} ({}), sum: {}",
                transactionRequest.ibanFrom(),
                transactionRequest.currencyCodeFrom(),
                transactionRequest.ibanTo(),
                transactionRequest.currencyCodeTo(),
                transactionRequest.sum());

        var transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NoSuchTransactionException(transactionId));
//        Thread.sleep(30000);


        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        var transactionRequestHttpEntity = new HttpEntity<>(transactionRequest, headers);
        var externalPaymentApiResponse = restTemplate.postForObject(appConfigVariables.getUrl(), transactionRequestHttpEntity, TransactionResponseDto.class);

        if (externalPaymentApiResponse != null && "SUCCESS".equalsIgnoreCase(externalPaymentApiResponse.status())) {
            transaction.setStatus(Status.PAID);
        } else {
            transaction.setStatus(Status.ERROR);
        }
        return externalPaymentApiResponse;
//        return null;
    }

    public Long createTransaction(Account accountFrom, Account accountTo, Long sum) {
        var currentTransaction = Transaction.builder()
                .accountFrom(accountFrom)
                .accountTo(accountTo)
                .currencyFrom(accountFrom.getCurrency())
                .currencyTo(accountTo.getCurrency())
                .status(Status.NEW)
                .changedAt(LocalDateTime.now())
                .sum(sum)
                .build();

        return transactionRepository.save(currentTransaction).getId();
    }

    public void checkAccountBalance(Account account, Double sum) {
        if (account.getBalance() < sum * 100) {
            throw new NotEnoughMoneyOnBalanceException(account.getIban());
        }
    }

    public Account checkAccount(String iban, String currencyCode) {
        var account = accountRepository.getAccountByIban(iban)
                .orElseThrow(() -> new NoSuchAccountException(iban));
        if (!Objects.equals(account.getCurrency().getCode(), currencyCode)) {
            throw new NoSuchAccountException(iban);
        }
        return account;
    }
}
