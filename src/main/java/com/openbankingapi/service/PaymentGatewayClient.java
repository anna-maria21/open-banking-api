package com.openbankingapi.service;

import com.openbankingapi.dto.TransactionRequestDto;
import com.openbankingapi.dto.TransactionResponseDto;
import com.openbankingapi.properties.AppConfigVariables;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class PaymentGatewayClient {
    private final RestTemplate restTemplate;
    private final AppConfigVariables config;

    public TransactionResponseDto initiate(TransactionRequestDto request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TransactionRequestDto> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(config.getUrl(), entity, TransactionResponseDto.class);
    }
}
