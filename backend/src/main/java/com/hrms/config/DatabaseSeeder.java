package com.hrms.config;

import com.hrms.entity.Role;
import com.hrms.entity.User;
import com.hrms.entity.UserRole;
import com.hrms.repository.RoleRepository;
import com.hrms.repository.UserRepository;
import com.hrms.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

// ==============================================================================
// DATABASE INITIALIZATION / SEEDER
// ==============================================================================
// Automatically bootstraps default system roles and creates the initial
// Super Admin user if the database is empty. Prevents bootstrapping lockouts.
// ==============================================================================

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedRoles();
        seedSuperAdmin();
    }

    private void seedRoles() {
        if (roleRepository.count() == 0) {
            log.info("No roles found in database. Seeding default roles...");

            Role superAdmin = Role.builder()
                    .name("ROLE_SUPER_ADMIN")
                    .description("Super Administrator with unrestricted access")
                    .build();
            superAdmin.setCreatedBy("SYSTEM_SEEDER");
            superAdmin.setStatus(1);
            superAdmin.setDeletedStatus(0);
            roleRepository.save(superAdmin);

            Role admin = Role.builder()
                    .name("ROLE_ADMIN")
                    .description("HR Administrator who manages employee profiles and master data")
                    .build();
            admin.setCreatedBy("SYSTEM_SEEDER");
            admin.setStatus(1);
            admin.setDeletedStatus(0);
            roleRepository.save(admin);

            Role staff = Role.builder()
                    .name("ROLE_STAFF")
                    .description("Standard employee with basic profile and attendance access")
                    .build();
            staff.setCreatedBy("SYSTEM_SEEDER");
            staff.setStatus(1);
            staff.setDeletedStatus(0);
            roleRepository.save(staff);

            log.info("Default roles seeded successfully!");
        }
    }

    private void seedSuperAdmin() {
        if (userRepository.count() == 0) {
            log.info("No users found in database. Seeding default Super Admin user...");

            // 1. Create Super Admin User
            User superAdminUser = User.builder()
                    .username("superadmin")
                    .email("superadmin@hrms.com")
                    .password(passwordEncoder.encode("superadmin123")) // Default password
                    .role("ROLE_SUPER_ADMIN")
                    .build();
            superAdminUser.setCreatedBy("SYSTEM_SEEDER");
            superAdminUser.setStatus(1);
            superAdminUser.setDeletedStatus(0);
            User savedUser = userRepository.save(superAdminUser);

            // 2. Find ROLE_SUPER_ADMIN Entity
            Role superAdminRole = roleRepository.findByNameAndDeletedStatus("ROLE_SUPER_ADMIN", 0)
                    .orElseThrow(() -> new IllegalStateException("ROLE_SUPER_ADMIN not found after role seeding!"));

            // 3. Map user to role in junction table
            UserRole userRole = UserRole.builder()
                    .userId(savedUser.getId())
                    .roleId(superAdminRole.getId())
                    .build();
            userRole.setCreatedBy("SYSTEM_SEEDER");
            userRole.setStatus(1);
            userRole.setDeletedStatus(0);
            userRoleRepository.save(userRole);

            log.info("Default Super Admin user (username: superadmin, password: superadmin123) seeded successfully!");
        }
    }
}
