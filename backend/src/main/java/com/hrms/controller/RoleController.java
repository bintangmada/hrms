package com.hrms.controller;

import com.hrms.dto.RoleRequest;
import com.hrms.dto.RoleResponse;
import com.hrms.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// ==============================================================================
// ROLE CONTROLLER
// ==============================================================================
// Exposes REST APIs for Role Master Data CRUD operations.
// Strict compliance: Only GET and POST methods are used. No PUT or DELETE.
// ==============================================================================

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Tag(name = "Role Management", description = "Endpoints for managing Role master data (GET and POST only)")
public class RoleController {

    private final RoleService roleService;

    // Helper to extract the authenticated username for the audit trail
    private String getCurrentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "SYSTEM";
    }

    // ENDPOINT: GET http://localhost:8020/api/v1/roles
    @Operation(summary = "Get list of all active roles")
    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> response = roleService.getAllActiveRoles();
        return ResponseEntity.ok(response);
    }

    // ENDPOINT: GET http://localhost:8020/api/v1/roles/{id}
    @Operation(summary = "Get active role details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id) {
        RoleResponse response = roleService.getRoleById(id);
        return ResponseEntity.ok(response);
    }

    // ENDPOINT: POST http://localhost:8020/api/v1/roles
    @Operation(summary = "Create a new role")
    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
        RoleResponse response = roleService.createRole(request, getCurrentUsername());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ENDPOINT: POST http://localhost:8020/api/v1/roles/{id}/update
    @Operation(summary = "Update an existing role")
    @PostMapping("/{id}/update")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        RoleResponse response = roleService.updateRole(id, request, getCurrentUsername());
        return ResponseEntity.ok(response);
    }

    // ENDPOINT: POST http://localhost:8020/api/v1/roles/{id}/delete
    @Operation(summary = "Soft-delete an existing role")
    @PostMapping("/{id}/delete")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        String result = roleService.deleteRole(id, getCurrentUsername());
        return ResponseEntity.ok(result);
    }
}
