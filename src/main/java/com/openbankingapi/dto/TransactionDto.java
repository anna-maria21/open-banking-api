package com.openbankingapi.dto;

import com.openbankingapi.entity.Status;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TransactionDto(Long id,
                             Long accountIdFrom,
                             String currencyIdFrom,
                             Long accountIdTo,
                             String currencyIdTo,
                             Double sum,
                             LocalDateTime changedAt,
                             Status status) {
}
