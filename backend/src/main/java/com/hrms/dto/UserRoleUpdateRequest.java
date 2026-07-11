package com.hrms.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRoleUpdateRequest {
    @NotEmpty(message = "At least one role ID must be selected")
    private List<Long> roleIds;
}
