package org.demchenko.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(HttpStatus status, String message) {
        super(message);
    }
}
