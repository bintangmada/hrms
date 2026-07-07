package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;

// ==============================================================================
// USER ENTITY
// ==============================================================================
// Represents users who can register and log in to the HRMS application.
// ==============================================================================

@Entity
@Table(name = "users") // "users" is a safe database table name in PostgreSQL
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password; // Will store BCrypt hashed password

    @Column(nullable = false, length = 30)
    private String role; // e.g. "ROLE_ADMIN", "ROLE_STAFF"

    @Builder.Default
    @Column(name = "email_verified", nullable = false)
    private Integer emailVerified = 0; // 0 = Unverified, 1 = Verified

    @Column(name = "verification_token", length = 100)
    private String verificationToken;
}
