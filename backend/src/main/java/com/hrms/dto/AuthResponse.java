package com.hrms.dto;

import lombok.*;

// ==============================================================================
// AUTH RESPONSE DTO
// ==============================================================================
// Holds authentication results (token and profile basic info) to return to clients.
// ==============================================================================

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private String username;
    private String email;
    private String role;
}
