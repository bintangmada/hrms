package com.hrms.service;

import com.hrms.dto.RoleRequest;
import com.hrms.dto.RoleResponse;
import java.util.List;

// ==============================================================================
// ROLE SERVICE INTERFACE
// ==============================================================================
// Defines CRUD operations for Role Master Data using only GET and POST methods.
// ==============================================================================

public interface RoleService {
    RoleResponse createRole(RoleRequest request, String currentUsername);
    RoleResponse updateRole(Long id, RoleRequest request, String currentUsername);
    String deleteRole(Long id, String currentUsername); // Soft delete
    RoleResponse getRoleById(Long id);
    List<RoleResponse> getAllActiveRoles();
}
