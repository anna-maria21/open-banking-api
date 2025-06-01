package com.openbankingapi.mapper;

import com.openbankingapi.dto.TransactionDto;
import com.openbankingapi.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionConverter implements TransactionMapper{
    @Override
    public Transaction dtoToTransaction(TransactionDto dto) {
        return null;
    }

    @Override
    public TransactionDto transactionToDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .accountIdFrom(transaction.getAccountFrom().getId())
                .currencyIdFrom(transaction.getCurrencyFrom().getCode())
                .accountIdTo(transaction.getAccountTo().getId())
                .currencyIdTo(transaction.getCurrencyTo().getCode())
                .sum(transaction.getSum() / 100.0)
                .status(transaction.getStatus())
                .changedAt(transaction.getChangedAt())
                .build();
    }
}
