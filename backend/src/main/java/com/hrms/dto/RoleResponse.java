package com.hrms.dto;

import lombok.*;
import java.time.LocalDateTime;

// ==============================================================================
// ROLE RESPONSE DTO
// ==============================================================================
// Returns formatted Role details along with audit trail information.
// ==============================================================================

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer status;
    private Integer deletedStatus;
}
