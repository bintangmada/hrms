package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

// ==============================================================================
// USER ROLE JUNCTION ENTITY (MANUAL MANY-TO-MANY)
// ==============================================================================
// Manages the many-to-many relationship between Users and Roles.
// Designed without JPA relationship annotations (@ManyToMany) to maintain simplicity.
// ==============================================================================

@Entity
@Table(name = "user_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRole extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "role_id", nullable = false)
    private Long roleId;
}
