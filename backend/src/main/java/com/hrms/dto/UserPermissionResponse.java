package com.hrms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPermissionResponse {
    private Long menuId;
    private String menuName;
    private String menuCode;
    private String menuPath;
    private Integer canRead;
    private Integer canWrite;
    private Integer canDelete;
}
