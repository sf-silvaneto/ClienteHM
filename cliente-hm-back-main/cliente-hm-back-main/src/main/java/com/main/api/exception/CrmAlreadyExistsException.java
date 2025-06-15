package com.main.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class CrmAlreadyExistsException extends RuntimeException {
    public CrmAlreadyExistsException(String message) {
        super(message);
    }
}