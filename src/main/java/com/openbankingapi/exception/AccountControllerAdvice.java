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
}
