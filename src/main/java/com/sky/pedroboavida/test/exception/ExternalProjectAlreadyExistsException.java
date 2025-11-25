package com.sky.pedroboavida.test.exception;

public class ExternalProjectAlreadyExistsException extends RuntimeException {
    public ExternalProjectAlreadyExistsException(String message) {
        super(message);
    }

    public ExternalProjectAlreadyExistsException(String projectId, Long userId) {
        super("External project with id '" + projectId + "' already exists for user with id: " + userId);
    }
}

