package com.openbankingapi.exception;

public class InvalidSumException extends RuntimeException {
    public InvalidSumException(Double sum) {
        super("Invalid sum: " + sum);
    }
}
