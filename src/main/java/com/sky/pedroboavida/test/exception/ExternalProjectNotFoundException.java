package com.sky.pedroboavida.test.exception;

public class ExternalProjectNotFoundException extends RuntimeException {
    public ExternalProjectNotFoundException(String projectId, Long userId) {
        super("External project with id '" + projectId + "' not found for user with id: " + userId);
    }
}

