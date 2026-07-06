package com.hrms.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {

    @NotBlank(message = "NIK cannot be empty")
    @Size(max = 30, message = "NIK must be less than 30 characters")
    private String nik;

    @NotBlank(message = "First name cannot be empty")
    @Size(max = 50, message = "First name must be less than 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must be less than 50 characters")
    private String lastName;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    @Size(max = 100, message = "Email must be less than 100 characters")
    private String email;

    @Size(max = 20, message = "Phone number must be less than 20 characters")
    private String phone;

    @Size(max = 50, message = "Position must be less than 50 characters")
    private String position;

    @Size(max = 50, message = "Department must be less than 50 characters")
    private String department;

    private LocalDate joinDate;

    private BigDecimal salary;

    private Long userId; // Linked User account ID (optional)
}
