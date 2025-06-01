package com.openbankingapi.dto;


public record TransactionResponseDto(
        String paymentId,
        String status,
        String message,
        String timestamp
) {}
