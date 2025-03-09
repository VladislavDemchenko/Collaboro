package org.demchenko.exception.handler;

import org.demchenko.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUserLoginAlreadyExistsException(UserLoginAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Login already exists");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUserEmailAlreadyExistsException(UserEmailAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Email already exists");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid password");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleNotFoundUserException(NotFoundUserException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User not found");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnexpected4XXException(Unexpected4XXException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.REQUEST_TIMEOUT.value(), "Unexpected 4XX exception");
        return new ResponseEntity<>(errorResponse, HttpStatus.REQUEST_TIMEOUT);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnexpected5XXException(Unexpected5XXException ex) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.REQUEST_TIMEOUT.value(), "Unexpected 5XX exception");
        return new ResponseEntity<>(errorResponse, HttpStatus.REQUEST_TIMEOUT);
    }
}
