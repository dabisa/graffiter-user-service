package com.graffitter.users.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class NotAllowed extends RuntimeException {
    public NotAllowed(String message) {
        super(message);
    }
}
