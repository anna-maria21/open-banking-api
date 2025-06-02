package com.openbankingapi.controller;

import com.openbankingapi.dto.TransactionRequestDto;
import com.openbankingapi.dto.TransactionResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/external")
@RequiredArgsConstructor
public class ExternalApiSimulator {

    @PostMapping("/simulate")
    public TransactionResponseDto simulatePaymentHandling(@RequestBody TransactionRequestDto transactionRequestDto) {
        TransactionResponseDto response;
        if (Objects.equals(transactionRequestDto.ibanFrom(), "UA21322313000000260000004111")) {
            response = new TransactionResponseDto(UUID.randomUUID().toString(), "ERROR", "API error", LocalDateTime.now().toString());
        } else {
            response = new TransactionResponseDto(UUID.randomUUID().toString(), "SUCCESS", null, LocalDateTime.now().toString());
        }
        return response;
    }
}
