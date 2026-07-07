package com.hrms.controller;

import com.hrms.dto.ApiResponse;
import com.hrms.dto.MenuRequest;
import com.hrms.dto.MenuResponse;
import com.hrms.service.MenuService;
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
@RequestMapping("/api/v1/menus")
@RequiredArgsConstructor
@Tag(name = "Menu Management", description = "Endpoints for managing Navigation Menus (GET and POST only)")
public class MenuController {

    private final MenuService menuService;

    private String getCurrentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "SYSTEM";
    }

    @Operation(summary = "Get list of all active menus")
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<MenuResponse>>> getAllMenus() {
        List<MenuResponse> response = menuService.getAllActiveMenus();
        return ResponseEntity.ok(ApiResponse.success("Menus retrieved successfully", response));
    }

    @Operation(summary = "Get active menu details by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<MenuResponse>> getMenuById(@PathVariable Long id) {
        MenuResponse response = menuService.getMenuById(id);
        return ResponseEntity.ok(ApiResponse.success("Menu details retrieved successfully", response));
    }

    @Operation(summary = "Create a new menu")
    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MenuResponse>> createMenu(@Valid @RequestBody MenuRequest request) {
        MenuResponse response = menuService.createMenu(request, getCurrentUsername());
        return new ResponseEntity<>(ApiResponse.success("Menu created successfully", response), HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing menu")
    @PostMapping("/{id}/update")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<MenuResponse>> updateMenu(@PathVariable Long id, @Valid @RequestBody MenuRequest request) {
        MenuResponse response = menuService.updateMenu(id, request, getCurrentUsername());
        return ResponseEntity.ok(ApiResponse.success("Menu updated successfully", response));
    }

    @Operation(summary = "Soft-delete an existing menu")
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteMenu(@PathVariable Long id) {
        String result = menuService.deleteMenu(id, getCurrentUsername());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
