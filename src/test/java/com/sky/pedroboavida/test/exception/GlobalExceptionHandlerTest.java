package com.sky.pedroboavida.test.exception;

import com.sky.pedroboavida.test.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest request;

    private static final String TEST_REQUEST_URI = "/api/users/1";

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn(TEST_REQUEST_URI);
    }

    @Test
    void handleUserNotFoundException_Success() {
        // Arrange
        Long userId = 1L;
        UserNotFoundException exception = new UserNotFoundException(userId);
        String expectedMessage = "User not found with id: " + userId;

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUserNotFoundException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getError());
        assertEquals(expectedMessage, errorResponse.getMessage());
        assertEquals(TEST_REQUEST_URI, errorResponse.getPath());
        assertNotNull(errorResponse.getTimestamp());
    }

    @Test
    void handleUserNotFoundException_WithCustomMessage() {
        // Arrange
        String customMessage = "Custom user not found message";
        UserNotFoundException exception = new UserNotFoundException(customMessage);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUserNotFoundException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(customMessage, errorResponse.getMessage());
    }

    @Test
    void handleUserAlreadyExistsException_Success() {
        // Arrange
        String email = "test@example.com";
        String message = "Please use a different email";
        UserAlreadyExistsException exception = new UserAlreadyExistsException(email, message);
        String expectedMessage = "User with email '" + email + "' already exists. " + message;

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleUserAlreadyExistsException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getStatus());
        assertEquals("Conflict", errorResponse.getError());
        assertEquals(expectedMessage, errorResponse.getMessage());
        assertEquals(TEST_REQUEST_URI, errorResponse.getPath());
    }

    @Test
    void handleExternalProjectNotFoundException_Success() {
        // Arrange
        String projectId = "project-123";
        Long userId = 1L;
        ExternalProjectNotFoundException exception = new ExternalProjectNotFoundException(projectId, userId);
        String expectedMessage = "External project with id '" + projectId + "' not found for user with id: " + userId;

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleExternalProjectNotFoundException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getError());
        assertEquals(expectedMessage, errorResponse.getMessage());
        assertEquals(TEST_REQUEST_URI, errorResponse.getPath());
    }

    @Test
    void handleExternalProjectAlreadyExistsException_Success() {
        // Arrange
        String projectId = "project-123";
        Long userId = 1L;
        ExternalProjectAlreadyExistsException exception = new ExternalProjectAlreadyExistsException(projectId, userId);
        String expectedMessage = "External project with id '" + projectId + "' already exists for user with id: " + userId;

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleExternalProjectAlreadyExistsException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.CONFLICT.value(), errorResponse.getStatus());
        assertEquals("Conflict", errorResponse.getError());
        assertEquals(expectedMessage, errorResponse.getMessage());
        assertEquals(TEST_REQUEST_URI, errorResponse.getPath());
    }

    @Test
    void handleExternalProjectAlreadyExistsException_WithCustomMessage() {
        // Arrange
        String customMessage = "Custom project already exists message";
        ExternalProjectAlreadyExistsException exception = new ExternalProjectAlreadyExistsException(customMessage);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleExternalProjectAlreadyExistsException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(customMessage, errorResponse.getMessage());
    }

    @Test
    void handleValidationException_Success() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        
        FieldError fieldError1 = new FieldError("createUserRequest", "email", "Email is required");
        FieldError fieldError2 = new FieldError("createUserRequest", "password", "Password must be between 6 and 129 characters");
        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse.getStatus());
        assertEquals("Validation Failed", errorResponse.getError());
        assertEquals("Request validation failed", errorResponse.getMessage());
        assertEquals(TEST_REQUEST_URI, errorResponse.getPath());
        
        assertNotNull(errorResponse.getDetails());
        assertEquals(2, errorResponse.getDetails().size());
        assertTrue(errorResponse.getDetails().contains("Email is required"));
        assertTrue(errorResponse.getDetails().contains("Password must be between 6 and 129 characters"));
    }

    @Test
    void handleValidationException_WithEmptyFieldErrors() {
        // Arrange
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        List<FieldError> emptyFieldErrors = Arrays.asList();

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(emptyFieldErrors);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertNotNull(errorResponse.getDetails());
        assertTrue(errorResponse.getDetails().isEmpty());
    }

    @Test
    void handleGenericException_Success() {
        // Arrange
        String exceptionMessage = "Unexpected error occurred";
        Exception exception = new RuntimeException(exceptionMessage);

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertEquals("Internal Server Error", errorResponse.getError());
        assertEquals(exceptionMessage, errorResponse.getMessage());
        assertEquals(TEST_REQUEST_URI, errorResponse.getPath());
    }

    @Test
    void handleGenericException_WithNullMessage() {
        // Arrange
        Exception exception = new RuntimeException();

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorResponse.getStatus());
        assertNull(errorResponse.getMessage());
    }

    @Test
    void handleGenericException_WithNullPointerException() {
        // Arrange
        NullPointerException exception = new NullPointerException("Null pointer error");

        // Act
        ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleGenericException(exception, request);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        ErrorResponse errorResponse = response.getBody();
        assertNotNull(errorResponse);
        assertEquals("Null pointer error", errorResponse.getMessage());
    }
}

