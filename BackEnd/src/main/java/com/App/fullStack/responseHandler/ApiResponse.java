package com.App.fullStack.responseHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private LocalDateTime timestamp;
    private String message;
    private T payload;

    public ApiResponse(boolean success, String message, T payload) {
        this.success = success;
        this.timestamp = LocalDateTime.now();
        this.message = message;
        this.payload = payload;
    }
}
