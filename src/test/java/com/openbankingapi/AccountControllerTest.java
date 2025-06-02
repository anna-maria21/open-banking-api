package com.openbankingapi;

import com.openbankingapi.controller.AccountController;
import com.openbankingapi.dto.TransactionRequestDto;
import com.openbankingapi.dto.TransactionResponseDto;
import com.openbankingapi.entity.Status;
import com.openbankingapi.entity.Transaction;
import com.openbankingapi.exception.NoSuchAccountException;
import com.openbankingapi.exception.NotEnoughMoneyOnBalanceException;
import com.openbankingapi.repository.AccountRepository;
import com.openbankingapi.repository.TransactionRepository;
import com.openbankingapi.service.PaymentGatewayClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
    AccountController accountController;

    @MockitoBean
    private PaymentGatewayClient paymentGatewayClient;


    @Test
    void getAccountBalance() {
        Double balance = accountController.getAccountBalance(EXISTING_IBAN);
        assertNotNull(balance);
        assertEquals(EXISTING_IBAN_BALANCE, balance);
        assertThrows(NoSuchAccountException.class, () -> accountController.getAccountBalance(NOT_EXISTING_IBAN));
    }

    @Test
    void getLastTransactions() {
        var pageable = PageRequest.of(0, 1);
        var transactions = accountController.getLastTransactions(EXISTING_IBAN, pageable);
        assertNotNull(transactions);
        assertEquals(1, transactions.size());
        var lastTransaction = transactions.getFirst();
        assertEquals(2L, lastTransaction.accountIdFrom());
        assertEquals(1L, lastTransaction.accountIdTo());

        pageable = PageRequest.of(0, 2);
        transactions = accountController.getLastTransactions(EXISTING_IBAN, pageable);
        assertEquals(2, transactions.size());
        lastTransaction = transactions.get(0);
        var previousTransaction = transactions.get(1);
        assertEquals(2L, lastTransaction.accountIdFrom());
        assertEquals(1L, previousTransaction.accountIdFrom());

        PageRequest finalPageable = pageable;
        assertThrows(NoSuchAccountException.class, () -> accountController.getLastTransactions(NOT_EXISTING_IBAN, finalPageable));
    }

    @Test
    void initiatePaymentWithWrongIban() {
        var transactionRequest = getTransactionRequest(NOT_EXISTING_IBAN, ANOTHER_EXISTING_IBAN, "UAH", 10.0);
        assertThrows(NoSuchAccountException.class, () -> accountController.initiatePayment(transactionRequest));
        var anotherTransactionRequest = getTransactionRequest(EXISTING_IBAN, NOT_EXISTING_IBAN, "UAH", 10.0);
        assertThrows(NoSuchAccountException.class, () -> accountController.initiatePayment(anotherTransactionRequest));
    }

    @Test
    void initiatePaymentWithWrongCurrency() {
        var transactionRequest = getTransactionRequest(EXISTING_IBAN, ANOTHER_EXISTING_IBAN, "EUR", 10.0);
        assertThrows(NoSuchAccountException.class, () -> accountController.initiatePayment(transactionRequest));
        var anotherTransactionRequest = getTransactionRequest(EXISTING_IBAN, ANOTHER_EXISTING_IBAN, "EUR", 10.0);
        assertThrows(NoSuchAccountException.class, () -> accountController.initiatePayment(anotherTransactionRequest));
    }

    @Test
    void initiatePaymentWithWrongSum() {
        var transactionRequest = getTransactionRequest(EXISTING_IBAN, ANOTHER_EXISTING_IBAN, "UAH", 100000.0);
        assertThrows(NotEnoughMoneyOnBalanceException.class, () -> accountController.initiatePayment(transactionRequest));
    }

    @Test
    void initiatePayment() {
        var transactionRequest = getTransactionRequest(EXISTING_IBAN, ANOTHER_EXISTING_IBAN, "UAH", 10.0);

        var mockSuccessfulResponse = new TransactionResponseDto(UUID.randomUUID().toString(), "SUCCESS", null, LocalDateTime.now().toString());
        when(paymentGatewayClient.initiate(transactionRequest)).thenReturn(mockSuccessfulResponse);
        var successfulResponse = accountController.initiatePayment(transactionRequest);
        assertEquals(mockSuccessfulResponse, successfulResponse);
        var sortedTransactions = transactionRepository.findAll()
                .stream().sorted(Comparator.comparing(Transaction::getChangedAt).reversed())
                .toList();
        assertEquals(3L, sortedTransactions.getFirst().getId());
        assertEquals(Status.PAID, sortedTransactions.getFirst().getStatus());

        var mockFailedResponse = new TransactionResponseDto(UUID.randomUUID().toString(), "ERROR", "Some API error", LocalDateTime.now().toString());
        when(paymentGatewayClient.initiate(transactionRequest)).thenReturn(mockFailedResponse);
        var failedResponse = accountController.initiatePayment(transactionRequest);
        assertEquals(mockFailedResponse, failedResponse);
        sortedTransactions = transactionRepository.findAll()
                .stream().sorted(Comparator.comparing(Transaction::getChangedAt).reversed())
                .toList();
        assertEquals(4L, sortedTransactions.getFirst().getId());
        assertEquals(Status.ERROR, sortedTransactions.getFirst().getStatus());
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
