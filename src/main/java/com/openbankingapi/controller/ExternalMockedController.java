package com.openbankingapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;


@RestController
@RequestMapping("/mock")
@RequiredArgsConstructor
public class ExternalMockedController {

    private final RestTemplate restTemplate;

    @GetMapping("/account/balance/{iban}")
    public Object getAccountBalance(@PathVariable String iban) {
        String url = "http://localhost:8080/api//accounts/" + iban + "/balance";

        return restTemplate.getForObject(url, Object.class);
    }

    @GetMapping("/account/transactions/{iban}")
    public List<Object> getAccountTransactions(@PathVariable String iban, Pageable pageable) {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString("http://localhost:8080/api/accounts/" + iban + "/transactions")
                .queryParam("page", pageable.getPageNumber())
                .queryParam("size", pageable.getPageSize());

        pageable.getSort().forEach(order ->
                builder.queryParam("sort", order.getProperty() + "," + order.getDirection())
        );
        String url = builder.toUriString();

        ResponseEntity<List<Object>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        return response.getBody();
    }
}
