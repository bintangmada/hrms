package com.hrms.service.impl;

import com.hrms.dto.AuthResponse;
import com.hrms.dto.LoginRequest;
import com.hrms.dto.RegisterRequest;
import com.hrms.entity.Role;
import com.hrms.entity.UserRole;
import com.hrms.entity.User;
import com.hrms.repository.RoleRepository;
import com.hrms.repository.UserRoleRepository;
import com.hrms.repository.UserRepository;
import com.hrms.security.JwtUtils;
import com.hrms.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// ==============================================================================
// AUTHENTICATION SERVICE IMPLEMENTATION
// ==============================================================================
// Implements manual register and login logic. Handles manual Many-to-Many
// relationships between Users and Roles without JPA relationship annotations.
// ==============================================================================

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    @Transactional
    public String registerUser(RegisterRequest request) {
        // 1. Verify username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        // 2. Verify email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered!");
        }

        // 3. Determine the user roles and verify existence
        List<Long> roleIds = request.getRoleIds();
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("At least one role ID must be selected!");
        }

        List<Role> rolesToAssign = new java.util.ArrayList<>();
        for (Long roleId : roleIds) {
            Role roleEntity = roleRepository.findByIdAndDeletedStatus(roleId, 0)
                    .orElseThrow(() -> new IllegalArgumentException("Role with ID " + roleId + " not found or deleted!"));
            rolesToAssign.add(roleEntity);
        }

        // Join role names for User's single-column fallback (comma-separated)
        String combinedRoles = rolesToAssign.stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));

        // 4. Create and save the new User
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hash password
                .role(combinedRoles)
                .build();

        user.setCreatedBy("REGISTRATION_FLOW");
        user.setStatus(1);
        user.setDeletedStatus(0);

        User savedUser = userRepository.save(user);

        // 5. Save Many-to-Many relations in junction table UserRole
        for (Role r : rolesToAssign) {
            UserRole userRole = UserRole.builder()
                    .userId(savedUser.getId())
                    .roleId(r.getId())
                    .build();
            userRole.setCreatedBy("REGISTRATION_FLOW");
            userRole.setStatus(1);
            userRole.setDeletedStatus(0);
            userRoleRepository.save(userRole);
        }

        return "User registered successfully!";
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse loginUser(LoginRequest request) {
        // 1. Find user by username or email
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password!"));

        // 2. Check if password matches
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password!");
        }

        // 3. Load roles dynamically from junction table (in case User.role is out of sync)
        List<UserRole> userRoles = userRoleRepository.findAllByUserIdAndDeletedStatus(user.getId(), 0);
        String combinedRoles;
        if (!userRoles.isEmpty()) {
            combinedRoles = userRoles.stream()
                    .map(ur -> roleRepository.findByIdAndDeletedStatus(ur.getRoleId(), 0))
                    .filter(java.util.Optional::isPresent)
                    .map(opt -> opt.get().getName())
                    .collect(Collectors.joining(","));
        } else {
            combinedRoles = user.getRole(); // fallback
        }

        // 4. Generate JWT Token
        String token = jwtUtils.generateToken(user.getUsername(), user.getEmail(), combinedRoles);

        // 5. Return AuthResponse
        return AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .role(combinedRoles)
                .build();
    }

    @Override
    @Transactional
    public String registerStaffUser(RegisterRequest request) {
        // 1. Verify username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken!");
        }

        // 2. Verify email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered!");
        }

        // 3. Find or seed ROLE_STAFF
        Role staffRole = roleRepository.findByNameAndDeletedStatus("ROLE_STAFF", 0)
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .name("ROLE_STAFF")
                            .description("Auto-generated default staff role")
                            .build();
                    newRole.setCreatedBy("REGISTRATION_FLOW");
                    newRole.setStatus(1);
                    newRole.setDeletedStatus(0);
                    return roleRepository.save(newRole);
                });

        // 4. Create and save the new User
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_STAFF")
                .build();

        user.setCreatedBy("REGISTRATION_FLOW");
        user.setStatus(1);
        user.setDeletedStatus(0);

        User savedUser = userRepository.save(user);

        // 5. Save relation in junction table UserRole
        UserRole userRole = UserRole.builder()
                .userId(savedUser.getId())
                .roleId(staffRole.getId())
                .build();
        userRole.setCreatedBy("REGISTRATION_FLOW");
        userRole.setStatus(1);
        userRole.setDeletedStatus(0);
        userRoleRepository.save(userRole);

        return "Staff registered successfully!";
    }
}
