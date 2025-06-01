package com.openbankingapi.exception;

public class NoSuchAccountException extends RuntimeException {

    public NoSuchAccountException(String iban) {
        super("Account with IBAN " + iban + " does not exist");
    }
}
