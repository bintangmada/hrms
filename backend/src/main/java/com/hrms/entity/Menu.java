package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

// ==============================================================================
// MENU ENTITY (MASTER DATA)
// ==============================================================================
// Represents navigation menus or system modules (e.g., FINANCE, ATTENDANCE).
// Strict compliance: No relationship annotations. Managed via service layer.
// ==============================================================================

@Entity
@Table(name = "menus")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name; // e.g. "Finance Management"

    @Column(nullable = false, unique = true, length = 50)
    private String code; // e.g. "FINANCE", "ATTENDANCE"

    @Column(nullable = false, length = 100)
    private String path; // e.g. "/finance", "/attendance"
}
