package com.openbankingapi.mapper;

import com.openbankingapi.dto.TransactionDto;
import com.openbankingapi.entity.Transaction;

public interface TransactionMapper {
    Transaction dtoToTransaction(TransactionDto dto);
    TransactionDto transactionToDto(Transaction transaction);
}
