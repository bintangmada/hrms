package com.hrms.controller;

import com.hrms.dto.ApiResponse;
import com.hrms.dto.UserResponse;
import com.hrms.dto.UserRoleUpdateRequest;
import com.hrms.entity.Role;
import com.hrms.entity.User;
import com.hrms.entity.UserRole;
import com.hrms.repository.RoleRepository;
import com.hrms.repository.UserRepository;
import com.hrms.repository.UserRoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing user accounts")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Operation(summary = "Get list of all active user accounts")
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> response = userRepository.findAllByDeletedStatus(0).stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole())
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("User accounts retrieved successfully", response));
    }

    @Operation(summary = "Get user details by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        User user = userRepository.findByIdAndDeletedStatus(id, 0)
                .orElseThrow(() -> new IllegalArgumentException("User not found or deleted!"));
        UserResponse response = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
        return ResponseEntity.ok(ApiResponse.success("User details retrieved successfully", response));
    }

    @Operation(summary = "Update user roles")
    @PostMapping("/{id}/roles")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    @Transactional
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRoles(
            @PathVariable Long id,
            @Valid @RequestBody UserRoleUpdateRequest request) {

        User user = userRepository.findByIdAndDeletedStatus(id, 0)
                .orElseThrow(() -> new IllegalArgumentException("User not found or deleted!"));

        // 1. Validate all roles exist
        List<Role> rolesToAssign = new java.util.ArrayList<>();
        for (Long roleId : request.getRoleIds()) {
            Role role = roleRepository.findByIdAndDeletedStatus(roleId, 0)
                    .orElseThrow(() -> new IllegalArgumentException("Role with ID " + roleId + " not found or deleted!"));
            rolesToAssign.add(role);
        }

        // 2. Fetch existing UserRole mappings and soft delete them (deleted_status = 1)
        List<UserRole> existingUserRoles = userRoleRepository.findAllByUserIdAndDeletedStatus(id, 0);
        for (UserRole ur : existingUserRoles) {
            ur.setDeletedStatus(1);
            ur.setUpdatedAt(java.time.LocalDateTime.now());
            ur.setUpdatedBy(getCurrentUsername());
            userRoleRepository.save(ur);
        }

        // 3. Create new UserRole entries
        for (Role role : rolesToAssign) {
            UserRole userRole = UserRole.builder()
                    .userId(id)
                    .roleId(role.getId())
                    .build();
            userRole.setCreatedBy(getCurrentUsername());
            userRole.setStatus(1);
            userRole.setDeletedStatus(0);
            userRoleRepository.save(userRole);
        }

        // 4. Update the combined roles on the User entity
        String combinedRoles = rolesToAssign.stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));
        user.setRole(combinedRoles);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        user.setUpdatedBy(getCurrentUsername());
        User updatedUser = userRepository.save(user);

        UserResponse response = UserResponse.builder()
                .id(updatedUser.getId())
                .username(updatedUser.getUsername())
                .email(updatedUser.getEmail())
                .role(updatedUser.getRole())
                .build();

        return ResponseEntity.ok(ApiResponse.success("User roles updated successfully", response));
    }

    private String getCurrentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "SYSTEM";
    }
}
