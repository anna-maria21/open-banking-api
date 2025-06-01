package com.openbankingapi.exception;

public class NoSuchAccountForTransactionException extends RuntimeException {
    public NoSuchAccountForTransactionException(String ibanFrom, String ibanTo) {
        super("Both of accounts " + ibanFrom + " and " + ibanTo + " are not found");
    }
}
