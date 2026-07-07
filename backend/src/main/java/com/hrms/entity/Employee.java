package com.hrms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

// ==============================================================================
// EMPLOYEE ENTITY
// ==============================================================================
// Represents the HR profile/biodata of a company employee.
// Relates to a User account for login and system access permissions.
// ==============================================================================

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String nik; // Nomor Induk Karyawan

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 50)
    private String position;

    @Column(length = 50)
    private String department;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal salary;

    @Column(name = "user_id", unique = true)
    private Long userId; // Linked login account ID (nullable)
}
