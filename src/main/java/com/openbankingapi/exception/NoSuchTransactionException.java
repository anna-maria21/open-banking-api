package com.openbankingapi.exception;

public class NoSuchTransactionException extends RuntimeException {
    public NoSuchTransactionException(Long transactionId) {
        super("No such transaction: " + transactionId);
    }
}
