package com.openbankingapi;

import com.openbankingapi.dto.TransactionDto;
import com.openbankingapi.dto.TransactionRequestDto;
import com.openbankingapi.dto.TransactionResponseDto;
import com.openbankingapi.entity.Status;
import com.openbankingapi.entity.Transaction;
import com.openbankingapi.exception.ErrorInfo;
import com.openbankingapi.repository.TransactionRepository;
import com.openbankingapi.service.PaymentGatewayClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class AccountControllerTest {
    public static final String EXISTING_IBAN = "UA21322313000000260000004111";
    public static final String ANOTHER_EXISTING_IBAN = "UA21322313000000260000004222";
    public static final String NOT_EXISTING_IBAN = "UA21322313000000260000004000";
    public static final double EXISTING_IBAN_BALANCE = 100.00;

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    private TestRestTemplate restTemplate;

    @MockitoBean
    private PaymentGatewayClient paymentGatewayClient;


    @Test
    void getAccountBalance() {
        var response = restTemplate.getForEntity("/api/accounts/" + EXISTING_IBAN + "/balance", Double.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Double balance = response.getBody();
        assertNotNull(balance);
        assertEquals(EXISTING_IBAN_BALANCE, balance);

        var errorResponse = restTemplate.getForEntity("/api/accounts/" + NOT_EXISTING_IBAN + "/balance", ErrorInfo.class);
        assertEquals(HttpStatus.NOT_FOUND, errorResponse.getStatusCode());
        assertNotNull(errorResponse.getBody());
        assertEquals("Account with IBAN " + NOT_EXISTING_IBAN + " does not exist", errorResponse.getBody().message());
    }

    @Test
    void getLastTransaction() {
        ResponseEntity<List<TransactionDto>> response = restTemplate.exchange(
                getPageableUrl("/api/accounts/" + EXISTING_IBAN + "/transactions", 0, 1),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var transactions = response.getBody();
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        var lastTransaction = transactions.getFirst();
        assertEquals(2L, lastTransaction.accountIdFrom());
        assertEquals(1L, lastTransaction.accountIdTo());
    }

    @Test
    void getLastTransactions() {
        ResponseEntity<List<TransactionDto>> response = restTemplate.exchange(
                getPageableUrl("/api/accounts/" + EXISTING_IBAN + "/transactions", 0, 2),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        var transactions = response.getBody();
        assertNotNull(transactions);
        assertEquals(2, transactions.size());
        var lastTransaction = transactions.get(0);
        var previousTransaction = transactions.get(1);
        assertEquals(2L, lastTransaction.accountIdFrom());
        assertEquals(1L, previousTransaction.accountIdFrom());
    }

    @Test
    void getTransactionsOfNotExistingAccount() {
        var response = restTemplate.exchange(
                getPageableUrl("/api/accounts/" + NOT_EXISTING_IBAN + "/transactions", 0, 2),
                HttpMethod.GET,
                null,
                ErrorInfo.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Account with IBAN " + NOT_EXISTING_IBAN + " does not exist", response.getBody().message());
    }

    @Test
    void initiatePaymentWithWrongIban() {
        var transactionRequest = getTransactionRequest(NOT_EXISTING_IBAN, ANOTHER_EXISTING_IBAN, "UAH", 10.0);
        var response = restTemplate.postForEntity(
                "/api/payments/initiate", transactionRequest, ErrorInfo.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Account with IBAN " + NOT_EXISTING_IBAN + " does not exist", response.getBody().message());

        var anotherTransactionRequest = getTransactionRequest(EXISTING_IBAN, NOT_EXISTING_IBAN, "UAH", 10.0);
        response = restTemplate.postForEntity(
                "/api/payments/initiate", anotherTransactionRequest, ErrorInfo.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Account with IBAN " + NOT_EXISTING_IBAN + " does not exist", response.getBody().message());
    }

    @Test
    void initiatePaymentWithWrongCurrency() {
        var transactionRequest = getTransactionRequest(EXISTING_IBAN, ANOTHER_EXISTING_IBAN, "EUR", 10.0);
        var response = restTemplate.postForEntity(
                "/api/payments/initiate", transactionRequest, ErrorInfo.class
        );
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Account with IBAN " + EXISTING_IBAN + " does not exist", response.getBody().message());
    }

    @Test
    void initiatePaymentWithWrongSum() {
        var transactionRequest = getTransactionRequest(EXISTING_IBAN, ANOTHER_EXISTING_IBAN, "UAH", 100000.0);
        var response = restTemplate.postForEntity(
                "/api/payments/initiate", transactionRequest, ErrorInfo.class
        );
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Not enough money on balance for account: " + EXISTING_IBAN, response.getBody().message());
    }

    @Test
    void initiatePayment() {
        var transactionRequest = getTransactionRequest(EXISTING_IBAN, ANOTHER_EXISTING_IBAN, "UAH", 10.0);

        var mockSuccessfulResponse = new TransactionResponseDto(UUID.randomUUID().toString(), "SUCCESS", null, LocalDateTime.now().toString());
        when(paymentGatewayClient.initiate(transactionRequest)).thenReturn(mockSuccessfulResponse);
        var successfulResponse = restTemplate.postForEntity(
                "/api/payments/initiate", transactionRequest, TransactionResponseDto.class
        );
        assertEquals(HttpStatus.OK, successfulResponse.getStatusCode());
        assertEquals(mockSuccessfulResponse, successfulResponse.getBody());
        var sortedTransactions = transactionRepository.findAll()
                .stream().sorted(Comparator.comparing(Transaction::getChangedAt).reversed())
                .toList();
        assertEquals(3L, sortedTransactions.getFirst().getId());
        assertEquals(Status.PAID, sortedTransactions.getFirst().getStatus());

        var mockFailedResponse = new TransactionResponseDto(UUID.randomUUID().toString(), "ERROR", "Some API error", LocalDateTime.now().toString());
        when(paymentGatewayClient.initiate(transactionRequest)).thenReturn(mockFailedResponse);
        var failedResponse = restTemplate.postForEntity(
                "/api/payments/initiate", transactionRequest, TransactionResponseDto.class
        );
        assertEquals(HttpStatus.OK, successfulResponse.getStatusCode());
        assertEquals(mockFailedResponse, failedResponse.getBody());
        sortedTransactions = transactionRepository.findAll()
                .stream().sorted(Comparator.comparing(Transaction::getChangedAt).reversed())
                .toList();
        assertEquals(4L, sortedTransactions.getFirst().getId());
        assertEquals(Status.ERROR, sortedTransactions.getFirst().getStatus());
    }

    private static String getPageableUrl(String uri, int page, int size) {
        var builder = UriComponentsBuilder
                .fromUriString(uri)
                .queryParam("page", page)
                .queryParam("size", size);
        return builder.toUriString();
    }

    private static TransactionRequestDto getTransactionRequest(String ibanFrom, String ibanTo, String currency, Double sum) {
        return TransactionRequestDto.builder()
                .ibanFrom(ibanFrom)
                .ibanTo(ibanTo)
                .sum(sum)
                .currencyCodeFrom(currency)
                .currencyCodeTo(currency)
                .build();
    }
}
