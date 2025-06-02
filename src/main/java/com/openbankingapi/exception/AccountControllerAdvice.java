package com.openbankingapi.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class AccountControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchAccountException.class)
    @ResponseBody ErrorInfo
    handleNoSuchAccountException(HttpServletRequest request, NoSuchAccountException ex) {
        return new ErrorInfo(request.getRequestURI(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchAccountForTransactionException.class)
    @ResponseBody ErrorInfo
    handleNoSuchAccountForTransactionException(HttpServletRequest request, NoSuchAccountForTransactionException ex) {
        return new ErrorInfo(request.getRequestURI(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidSumException.class)
    @ResponseBody ErrorInfo
    handleInvalidSumException(HttpServletRequest request, InvalidSumException ex) {
        return new ErrorInfo(request.getRequestURI(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoSuchCurrencyException.class)
    @ResponseBody ErrorInfo
    handleNoSuchCurrencyException(HttpServletRequest request, NoSuchCurrencyException ex) {
        return new ErrorInfo(request.getRequestURI(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NotEnoughMoneyOnBalanceException.class)
    @ResponseBody ErrorInfo
    handleNotEnoughMoneyOnBalanceException(HttpServletRequest request, NotEnoughMoneyOnBalanceException ex) {
        return new ErrorInfo(request.getRequestURI(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PaymentProcessingException.class)
    @ResponseBody ErrorInfo
    handlePaymentProcessingException(HttpServletRequest request, PaymentProcessingException ex) {
        return new ErrorInfo(request.getRequestURI(), ex.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(NoSuchTransactionException.class)
    @ResponseBody ErrorInfo
    handleNoSuchTransactionException(HttpServletRequest request, NoSuchTransactionException ex) {
        return new ErrorInfo(request.getRequestURI(), ex.getMessage());
    }
}
