package com.openbankingapi.exception;

public class PaymentProcessingException extends RuntimeException {
    public PaymentProcessingException(Long transactionId) {
        super("Payment processing error for transaction with id " + transactionId);
    }
}
