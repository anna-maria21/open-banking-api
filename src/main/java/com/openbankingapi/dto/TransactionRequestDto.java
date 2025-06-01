package com.openbankingapi.dto;

public record TransactionRequestDto (
        String ibanFrom,
        String ibanTo,
        Double sum,
        String currencyCodeFrom
) {
}
