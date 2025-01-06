package org.demchenko.exception;

public class UserAuthenticationRequest extends RuntimeException {
    public UserAuthenticationRequest(String message) {
        super(message);
    }
}
