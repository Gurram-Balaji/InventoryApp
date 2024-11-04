package com.App.fullStack.utility;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.App.fullStack.responseHandler.ApiResponse;

import static org.junit.jupiter.api.Assertions.*;

class APIResponseForFoundOrNotTest {

    @Test
    void testGenerateResponse_NonNullPayload() {
        // Arrange
        String successMessage = "Data found";
        String errorMessage = "Data not found";
        String payload = "Sample Data"; // Non-null payload

        // Act
        ResponseEntity<ApiResponse<String>> response = APIResponseForFoundOrNot.generateResponse(payload, successMessage, errorMessage);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals(successMessage + ".", response.getBody().getMessage());
        assertEquals(payload, response.getBody().getPayload());
    }

    @Test
    void testGenerateResponse_NullPayload() {
        // Arrange
        String successMessage = "Data found";
        String errorMessage = "Data not found";
        String payload = null; // Null payload

        // Act
        ResponseEntity<ApiResponse<String>> response = APIResponseForFoundOrNot.generateResponse(payload, successMessage, errorMessage);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals(errorMessage + ".", response.getBody().getMessage());
        assertNull(response.getBody().getPayload());
    }

    @Test
    void testGenerateResponse_EmptyListPayload() {
        // Arrange
        String successMessage = "Data found";
        String errorMessage = "Data not found";
        List<String> payload = Collections.emptyList(); // Empty list payload

        // Act
        ResponseEntity<ApiResponse<List<String>>> response = APIResponseForFoundOrNot.generateResponse(payload, successMessage, errorMessage);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals(errorMessage + ".", response.getBody().getMessage());
        assertNull(response.getBody().getPayload());
    }

    @Test
    void testGenerateResponse_NonEmptyListPayload() {
        // Arrange
        String successMessage = "Data found";
        String errorMessage = "Data not found";
        List<String> payload = List.of("Data 1", "Data 2"); // Non-empty list payload

        // Act
        ResponseEntity<ApiResponse<List<String>>> response = APIResponseForFoundOrNot.generateResponse(payload, successMessage, errorMessage);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).isSuccess());
        assertEquals(successMessage + ".", response.getBody().getMessage());
        assertEquals(payload, response.getBody().getPayload());
    }
}
