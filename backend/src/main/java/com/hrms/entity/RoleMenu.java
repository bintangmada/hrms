package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

// ==============================================================================
// ROLE MENU JUNCTION ENTITY
// ==============================================================================
// Maps roles to menus with specific permission flags (canRead, canWrite, canDelete).
// Strict compliance: No relationship annotations. Managed via service layer.
// ==============================================================================

@Entity
@Table(name = "role_menus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleMenu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @Column(name = "can_read", nullable = false)
    private Integer canRead = 0; // 0 = No, 1 = Yes

    @Column(name = "can_write", nullable = false)
    private Integer canWrite = 0; // 0 = No, 1 = Yes

    @Column(name = "can_delete", nullable = false)
    private Integer canDelete = 0; // 0 = No, 1 = Yes
}
