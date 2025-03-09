package org.demchenko.exception;

import org.springframework.http.HttpStatus;

public class UserEmailAlreadyExistsException extends RuntimeException {
    public UserEmailAlreadyExistsException() {}
}
