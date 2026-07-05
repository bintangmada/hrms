package com.hrms.config;

import com.hrms.entity.Menu;
import com.hrms.entity.Role;
import com.hrms.entity.RoleMenu;
import com.hrms.entity.User;
import com.hrms.entity.UserRole;
import com.hrms.repository.MenuRepository;
import com.hrms.repository.RoleMenuRepository;
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
// Automatically bootstraps default system roles, menus, initial permissions,
// and creates the initial Super Admin user if the database is empty.
// ==============================================================================

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final MenuRepository menuRepository;
    private final RoleMenuRepository roleMenuRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedRoles();
        seedSuperAdmin();
        seedMenusAndPermissions();
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

    private void seedMenusAndPermissions() {
        if (menuRepository.count() == 0) {
            log.info("No menus found in database. Seeding default menus...");

            // 1. Create Menus
            Menu financeMenu = Menu.builder()
                    .name("Finance Management")
                    .code("FINANCE")
                    .path("/finance")
                    .build();
            financeMenu.setCreatedBy("SYSTEM_SEEDER");
            financeMenu.setStatus(1);
            financeMenu.setDeletedStatus(0);
            financeMenu = menuRepository.save(financeMenu);

            Menu attendanceMenu = Menu.builder()
                    .name("Attendance Management")
                    .code("ATTENDANCE")
                    .path("/attendance")
                    .build();
            attendanceMenu.setCreatedBy("SYSTEM_SEEDER");
            attendanceMenu.setStatus(1);
            attendanceMenu.setDeletedStatus(0);
            attendanceMenu = menuRepository.save(attendanceMenu);

            Menu employeeMenu = Menu.builder()
                    .name("Employee Management")
                    .code("EMPLOYEE")
                    .path("/employees")
                    .build();
            employeeMenu.setCreatedBy("SYSTEM_SEEDER");
            employeeMenu.setStatus(1);
            employeeMenu.setDeletedStatus(0);
            employeeMenu = menuRepository.save(employeeMenu);

            log.info("Default menus seeded successfully!");

            // 2. Fetch seeded Roles
            Role superAdminRole = roleRepository.findByNameAndDeletedStatus("ROLE_SUPER_ADMIN", 0).orElse(null);
            Role adminRole = roleRepository.findByNameAndDeletedStatus("ROLE_ADMIN", 0).orElse(null);
            Role staffRole = roleRepository.findByNameAndDeletedStatus("ROLE_STAFF", 0).orElse(null);

            if (superAdminRole != null && adminRole != null && staffRole != null) {
                log.info("Seeding default role-menu permissions...");

                // Super Admin permissions (Full access to all menus)
                saveRoleMenu(superAdminRole.getId(), financeMenu.getId(), 1, 1, 1);
                saveRoleMenu(superAdminRole.getId(), attendanceMenu.getId(), 1, 1, 1);
                saveRoleMenu(superAdminRole.getId(), employeeMenu.getId(), 1, 1, 1);

                // Admin permissions (Full access to Attendance/Employees, Read-only to Finance)
                saveRoleMenu(adminRole.getId(), financeMenu.getId(), 1, 0, 0);
                saveRoleMenu(adminRole.getId(), attendanceMenu.getId(), 1, 1, 1);
                saveRoleMenu(adminRole.getId(), employeeMenu.getId(), 1, 1, 1);

                // Staff permissions (Read-only to Attendance, no access to others)
                saveRoleMenu(staffRole.getId(), attendanceMenu.getId(), 1, 0, 0);

                log.info("Default role-menu permissions seeded successfully!");
            }
        }
    }

    private void saveRoleMenu(Long roleId, Long menuId, Integer read, Integer write, Integer delete) {
        RoleMenu roleMenu = RoleMenu.builder()
                .roleId(roleId)
                .menuId(menuId)
                .canRead(read)
                .canWrite(write)
                .canDelete(delete)
                .build();
        roleMenu.setCreatedBy("SYSTEM_SEEDER");
        roleMenu.setStatus(1);
        roleMenu.setDeletedStatus(0);
        roleMenuRepository.save(roleMenu);
    }
}
