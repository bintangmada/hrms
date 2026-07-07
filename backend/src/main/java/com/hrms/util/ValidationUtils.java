package com.hrms.util;

// ==============================================================================
// VALIDATION UTILITIES
// ==============================================================================
// Common validation rules to protect the API against empty strings, whitespaces,
// and default Swagger placeholder values ("string").
// ==============================================================================

public class ValidationUtils {

    /**
     * Validates that a string is neither null, empty, whitespace-only,
     * nor matches the default Swagger placeholder value "string" (case-insensitive).
     */
    public static void validateNotPlaceholder(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty or whitespace only!");
        }
        if ("string".equalsIgnoreCase(value.trim())) {
            throw new IllegalArgumentException(fieldName + " cannot contain the default Swagger placeholder 'string'!");
        }
    }

    /**
     * Validates optional fields, ensuring if they are present they do not match
     * the default Swagger placeholder value "string".
     */
    public static void validateOptionalNotPlaceholder(String value, String fieldName) {
        if (value != null && "string".equalsIgnoreCase(value.trim())) {
            throw new IllegalArgumentException(fieldName + " cannot contain the default Swagger placeholder 'string'!");
        }
    }
}
