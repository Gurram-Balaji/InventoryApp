package com.App.fullStack.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        webRequest = mock(WebRequest.class);
    }

    @Test
    void testHandleFoundException() {
        FoundException foundException = new FoundException("Resource not found");
        Mockito.when(webRequest.getDescription(false)).thenReturn("URI=/test");

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleProductNotFoundException(foundException, webRequest);

        ErrorResponse errorResponse = responseEntity.getBody();

        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("Resource not found", errorResponse.getMessage());
        assertEquals("URI=/test", errorResponse.getPath());
        assertEquals(LocalDateTime.now().getDayOfYear(), errorResponse.getTimestamp().getDayOfYear());
    }

    @Test
    void testHandleHttpMessageNotReadableException() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Malformed JSON request");
        Mockito.when(webRequest.getDescription(false)).thenReturn("URI=/test");

        ResponseEntity<ErrorResponse> responseEntity = globalExceptionHandler.handleHttpMessageNotReadableException(exception, webRequest);

        ErrorResponse errorResponse = responseEntity.getBody();

        assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCodeValue());
        assertEquals(HttpStatus.NOT_FOUND.value(), errorResponse.getStatus());
        assertEquals("Malformed JSON request", errorResponse.getMessage());
        assertEquals("URI=/test", errorResponse.getPath());
        assertEquals(LocalDateTime.now().getDayOfYear(), errorResponse.getTimestamp().getDayOfYear());
    }
}
