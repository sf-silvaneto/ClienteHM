package com.clientehm.exception; // Verifique se esta linha está correta

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) // 409 Conflict
public class CrmAlreadyExistsException extends RuntimeException {
    public CrmAlreadyExistsException(String message) {
        super(message);
    }
}