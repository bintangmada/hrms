package com.hrms.controller;

import com.hrms.dto.ApiResponse;
import com.hrms.dto.RoleMenuRequest;
import com.hrms.dto.RoleMenuResponse;
import com.hrms.dto.UserPermissionResponse;
import com.hrms.service.RoleMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role-menus")
@RequiredArgsConstructor
@Tag(name = "Role Menu Permission Management", description = "Endpoints for managing role-based menu permissions (GET and POST only)")
public class RoleMenuController {

    private final RoleMenuService roleMenuService;

    private String getCurrentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "SYSTEM";
    }

    @Operation(summary = "Assign or update menu permissions for a role")
    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<RoleMenuResponse>> assignPermission(@Valid @RequestBody RoleMenuRequest request) {
        RoleMenuResponse response = roleMenuService.assignPermission(request, getCurrentUsername());
        return new ResponseEntity<>(ApiResponse.success("Permission assigned successfully", response), HttpStatus.CREATED);
    }

    @Operation(summary = "Get all menu permissions assigned to a role")
    @GetMapping("/role/{roleId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<RoleMenuResponse>>> getPermissionsByRoleId(@PathVariable Long roleId) {
        List<RoleMenuResponse> response = roleMenuService.getPermissionsByRoleId(roleId);
        return ResponseEntity.ok(ApiResponse.success("Role permissions retrieved successfully", response));
    }

    @Operation(summary = "Get current authenticated user's combined menu permissions")
    @GetMapping("/my-permissions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<UserPermissionResponse>>> getMyPermissions() {
        String username = getCurrentUsername();
        List<UserPermissionResponse> response = roleMenuService.getUserPermissions(username);
        return ResponseEntity.ok(ApiResponse.success("User permissions retrieved successfully", response));
    }
}
