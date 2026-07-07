package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

// ==============================================================================
// ROLE REQUEST DTO
// ==============================================================================
// Holds role input details for create and update endpoints.
// ==============================================================================

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequest {

    @NotBlank(message = "Role name cannot be empty")
    @Size(min = 3, max = 50, message = "Role name must be between 3 and 50 characters")
    private String name; // e.g. "ROLE_ADMIN" or "ADMIN"

    @Size(max = 255, message = "Description must be less than 255 characters")
    private String description;
}
