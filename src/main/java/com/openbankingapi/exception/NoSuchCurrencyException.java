package com.openbankingapi.exception;

public class NoSuchCurrencyException extends RuntimeException {
    public NoSuchCurrencyException(String currencyCodeFrom) {
        super("No such currency found for " + currencyCodeFrom);
    }
}
