package com.hrms.dto;

import lombok.*;
import java.time.LocalDateTime;

// ==============================================================================
// STANDARDIZED API RESPONSE ENVELOPE
// ==============================================================================
// Unified response structure for all API endpoints, including success status,
// messaging, payload data, validation errors, and response timestamp.
// ==============================================================================

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private Object errors;
    
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // Helper for successful responses with data payload
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    // Helper for successful responses without data payload
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    // Helper for error responses with details
    public static <T> ApiResponse<T> error(String message, Object errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }

    // Helper for simple error responses without detailed map
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
