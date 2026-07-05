package com.hrms.service;

import com.hrms.dto.RoleMenuRequest;
import com.hrms.dto.RoleMenuResponse;
import com.hrms.dto.UserPermissionResponse;
import java.util.List;

public interface RoleMenuService {
    RoleMenuResponse assignPermission(RoleMenuRequest request, String currentUsername);
    List<RoleMenuResponse> getPermissionsByRoleId(Long roleId);
    List<UserPermissionResponse> getUserPermissions(String username);
}
