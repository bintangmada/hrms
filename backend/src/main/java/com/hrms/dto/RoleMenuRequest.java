package com.hrms.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleMenuRequest {

    @NotNull(message = "Role ID cannot be null")
    private Long roleId;

    @NotNull(message = "Menu ID cannot be null")
    private Long menuId;

    @NotNull(message = "canRead flag cannot be null")
    @Min(value = 0, message = "canRead flag must be 0 or 1")
    @Max(value = 1, message = "canRead flag must be 0 or 1")
    private Integer canRead;

    @NotNull(message = "canWrite flag cannot be null")
    @Min(value = 0, message = "canWrite flag must be 0 or 1")
    @Max(value = 1, message = "canWrite flag must be 0 or 1")
    private Integer canWrite;

    @NotNull(message = "canDelete flag cannot be null")
    @Min(value = 0, message = "canDelete flag must be 0 or 1")
    @Max(value = 1, message = "canDelete flag must be 0 or 1")
    private Integer canDelete;
}
