package com.App.fullStack.responseHandler;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class ApiResponseTest {


    @Test
    void testParameterizedConstructor() {
        // Arrange
        boolean success = true;
        String message = "Operation completed successfully.";
        String payload = "Sample Data";

        // Act
        ApiResponse<String> response = new ApiResponse<>(success, message, payload);

        // Assert
        assertTrue(response.isSuccess(), "Success should be true");
        assertNotNull(response.getTimestamp(), "Timestamp should not be null");
        assertEquals(message, response.getMessage(), "Messages should match");
        assertEquals(payload, response.getPayload(), "Payloads should match");
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        ApiResponse<String> response = new ApiResponse<>();
        boolean success = false;
        String message = "Error occurred.";
        String payload = "Error details";

        // Act
        response.setSuccess(success);
        response.setMessage(message);
        response.setPayload(payload);
        LocalDateTime timestampBeforeSetting = response.getTimestamp();

        // Assert
        assertEquals(success, response.isSuccess(), "Success should match");
        assertEquals(message, response.getMessage(), "Messages should match");
        assertEquals(payload, response.getPayload(), "Payloads should match");

        // Check if timestamp is not updated when setting other fields
        assertEquals(timestampBeforeSetting, response.getTimestamp(), "Timestamp should remain unchanged after setting other fields");
    }
}
