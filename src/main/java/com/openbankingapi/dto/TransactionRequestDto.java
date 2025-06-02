package com.openbankingapi.dto;

import lombok.Builder;

@Builder
public record TransactionRequestDto(
        String ibanFrom,
        String ibanTo,
        Double sum,
        String currencyCodeFrom,
        String currencyCodeTo
) {
}
