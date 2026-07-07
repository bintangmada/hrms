package com.hrms.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleMenuResponse {
    private Long id;
    private Long roleId;
    private Long menuId;
    private Integer canRead;
    private Integer canWrite;
    private Integer canDelete;

    // Audit Trail
    private String createdBy;
    private String updatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer status;
    private Integer deletedStatus;
}
