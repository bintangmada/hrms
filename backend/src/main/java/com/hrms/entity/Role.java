package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

// ==============================================================================
// ROLE ENTITY (MASTER DATA)
// ==============================================================================
// Represents user authorization roles (e.g. ROLE_ADMIN, ROLE_STAFF).
// Inherits audit trail and soft delete fields from BaseEntity.
// ==============================================================================

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // e.g. "ROLE_ADMIN", "ROLE_STAFF"

    @Column(length = 255)
    private String description;
}
