package org.demchenko.exception;

import org.springframework.http.HttpStatus;

public class UserLoginAlreadyExistsException extends RuntimeException {
    public UserLoginAlreadyExistsException() {}
}
