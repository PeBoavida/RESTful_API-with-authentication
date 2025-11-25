package com.sky.pedroboavida.test.exception;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email, String message) {
        super("User with email '" + email + "' already exists. " + message);
    }
}

