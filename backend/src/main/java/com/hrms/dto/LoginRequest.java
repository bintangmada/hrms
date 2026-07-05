package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

// ==============================================================================
// LOGIN REQUEST DTO
// ==============================================================================
// Holds user input details for login endpoint validation.
// ==============================================================================

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Username or email cannot be empty")
    private String usernameOrEmail;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}
