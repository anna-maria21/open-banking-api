package com.openbankingapi.service;

import com.openbankingapi.repository.TransactionRepository;
import com.openbankingapi.dto.TransactionDto;
import com.openbankingapi.exception.NoSuchAccountException;
import com.openbankingapi.mapper.TransactionConverter;
import com.openbankingapi.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionConverter transactionConverter;

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
}
