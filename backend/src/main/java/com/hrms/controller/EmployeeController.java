package com.hrms.controller;

import com.hrms.dto.ApiResponse;
import com.hrms.dto.EmployeeRequest;
import com.hrms.dto.EmployeeResponse;
import com.hrms.service.EmployeeService;
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

// ==============================================================================
// EMPLOYEE CONTROLLER
// ==============================================================================
// Exposes REST APIs for Employee Master Data operations.
// Strict compliance: Only GET and POST methods are used. No PUT or DELETE.
// ==============================================================================

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "Endpoints for managing Employee profiles (GET and POST only)")
public class EmployeeController {

    private final EmployeeService employeeService;

    // Helper to extract the authenticated username for the audit trail
    private String getCurrentUsername() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName();
        }
        return "SYSTEM";
    }

    // ENDPOINT: GET http://localhost:8020/api/v1/employees
    @Operation(summary = "Get list of all active employee profiles")
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<EmployeeResponse>>> getAllEmployees() {
        List<EmployeeResponse> response = employeeService.getAllEmployees();
        return ResponseEntity.ok(ApiResponse.success("Employee profiles retrieved successfully", response));
    }

    // ENDPOINT: GET http://localhost:8020/api/v1/employees/{id}
    @Operation(summary = "Get active employee profile by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeById(@PathVariable Long id) {
        EmployeeResponse response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(ApiResponse.success("Employee details retrieved successfully", response));
    }

    // ENDPOINT: GET http://localhost:8020/api/v1/employees/user/{userId}
    @Operation(summary = "Get active employee profile by associated User ID")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getEmployeeByUserId(@PathVariable Long userId) {
        EmployeeResponse response = employeeService.getEmployeeByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Employee details retrieved successfully", response));
    }

    // ENDPOINT: POST http://localhost:8020/api/v1/employees
    @Operation(summary = "Create a new employee profile")
    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request, getCurrentUsername());
        return new ResponseEntity<>(ApiResponse.success("Employee profile created successfully", response), HttpStatus.CREATED);
    }

    // ENDPOINT: POST http://localhost:8020/api/v1/employees/{id}/update
    @Operation(summary = "Update an existing employee profile")
    @PostMapping("/{id}/update")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse response = employeeService.updateEmployee(id, request, getCurrentUsername());
        return ResponseEntity.ok(ApiResponse.success("Employee profile updated successfully", response));
    }

    // ENDPOINT: POST http://localhost:8020/api/v1/employees/{id}/delete
    @Operation(summary = "Soft-delete an existing employee profile")
    @PostMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteEmployee(@PathVariable Long id) {
        String result = employeeService.deleteEmployee(id, getCurrentUsername());
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
