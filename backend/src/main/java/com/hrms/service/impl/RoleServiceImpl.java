package com.hrms.service.impl;

import com.hrms.dto.RoleRequest;
import com.hrms.dto.RoleResponse;
import com.hrms.entity.Role;
import com.hrms.repository.RoleRepository;
import com.hrms.service.RoleService;
import com.hrms.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// ==============================================================================
// ROLE SERVICE IMPLEMENTATION
// ==============================================================================
// Implements business logic for Role CRUD, enforcing role name normalization,
// audit trail logging, and soft-delete filtering (deletedStatus = 0).
// ==============================================================================

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request, String currentUsername) {
        // Enforce validation to prevent blank values or placeholder 'string'
        ValidationUtils.validateNotPlaceholder(request.getName(), "Role name");
        ValidationUtils.validateOptionalNotPlaceholder(request.getDescription(), "Role description");

        String roleName = normalizeRoleName(request.getName());

        // Check if role name already exists in active roles
        if (roleRepository.existsByNameAndDeletedStatus(roleName, 0)) {
            throw new IllegalArgumentException("Role name already exists!");
        }

        Role role = Role.builder()
                .name(roleName)
                .description(request.getDescription())
                .build();

        // Audit Trail Setup
        role.setCreatedBy(currentUsername != null ? currentUsername : "SYSTEM");
        role.setStatus(1);
        role.setDeletedStatus(0);

        Role savedRole = roleRepository.save(role);
        return mapToResponse(savedRole);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long id, RoleRequest request, String currentUsername) {
        // Enforce validation to prevent blank values or placeholder 'string'
        ValidationUtils.validateNotPlaceholder(request.getName(), "Role name");
        ValidationUtils.validateOptionalNotPlaceholder(request.getDescription(), "Role description");

        Role role = roleRepository.findByIdAndDeletedStatus(id, 0)
                .orElseThrow(() -> new IllegalArgumentException("Role not found or has been deleted!"));

        String roleName = normalizeRoleName(request.getName());

        // If name changes, verify uniqueness
        if (!role.getName().equals(roleName) && roleRepository.existsByNameAndDeletedStatus(roleName, 0)) {
            throw new IllegalArgumentException("Role name already exists!");
        }

        role.setName(roleName);
        role.setDescription(request.getDescription());
        role.setUpdatedBy(currentUsername != null ? currentUsername : "SYSTEM");
        role.setUpdatedAt(LocalDateTime.now());

        Role updatedRole = roleRepository.save(role);
        return mapToResponse(updatedRole);
    }

    @Override
    @Transactional
    public String deleteRole(Long id, String currentUsername) {
        Role role = roleRepository.findByIdAndDeletedStatus(id, 0)
                .orElseThrow(() -> new IllegalArgumentException("Role not found or has been deleted!"));

        // Perform Soft Delete
        role.setDeletedStatus(1); // 1 = Soft Deleted
        role.setDeletedBy(currentUsername != null ? currentUsername : "SYSTEM");
        role.setDeletedAt(LocalDateTime.now());

        roleRepository.save(role);
        return "Role deleted successfully!";
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findByIdAndDeletedStatus(id, 0)
                .orElseThrow(() -> new IllegalArgumentException("Role not found or has been deleted!"));
        return mapToResponse(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllActiveRoles() {
        return roleRepository.findAllByDeletedStatus(0).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper: Normalize name to uppercase and prefix with ROLE_ (e.g. "admin" -> "ROLE_ADMIN")
    private String normalizeRoleName(String name) {
        if (name == null) return null;
        String trimmed = name.trim().toUpperCase();
        if (!trimmed.startsWith("ROLE_")) {
            return "ROLE_" + trimmed;
        }
        return trimmed;
    }

    // Helper: Map Entity to Response DTO
    private RoleResponse mapToResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .createdBy(role.getCreatedBy())
                .updatedBy(role.getUpdatedBy())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .status(role.getStatus())
                .deletedStatus(role.getDeletedStatus())
                .build();
    }
}
