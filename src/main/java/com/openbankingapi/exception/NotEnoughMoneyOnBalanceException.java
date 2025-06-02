package com.openbankingapi.exception;

public class NotEnoughMoneyOnBalanceException extends RuntimeException {
    public NotEnoughMoneyOnBalanceException(String iban) {
        super("Not enough money on balance for account: " + iban);
    }
}
