package com.hrms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuRequest {

    @NotBlank(message = "Menu name cannot be empty")
    @Size(max = 100, message = "Menu name must be less than 100 characters")
    private String name;

    @NotBlank(message = "Menu code cannot be empty")
    @Size(max = 50, message = "Menu code must be less than 50 characters")
    private String code;

    @NotBlank(message = "Menu path cannot be empty")
    @Size(max = 100, message = "Menu path must be less than 100 characters")
    private String path;
}
