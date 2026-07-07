package com.hrms.service.impl;

import com.hrms.dto.RoleMenuRequest;
import com.hrms.dto.RoleMenuResponse;
import com.hrms.dto.UserPermissionResponse;
import com.hrms.entity.Menu;
import com.hrms.entity.Role;
import com.hrms.entity.RoleMenu;
import com.hrms.entity.User;
import com.hrms.entity.UserRole;
import com.hrms.repository.MenuRepository;
import com.hrms.repository.RoleRepository;
import com.hrms.repository.RoleMenuRepository;
import com.hrms.repository.UserRepository;
import com.hrms.repository.UserRoleRepository;
import com.hrms.service.RoleMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.hrms.service.EmailService;

@Service
@RequiredArgsConstructor
public class RoleMenuServiceImpl implements RoleMenuService {

    private final RoleMenuRepository roleMenuRepository;
    private final RoleRepository roleRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public RoleMenuResponse assignPermission(RoleMenuRequest request, String currentUsername) {
        // 1. Verify Role exists and is active
        Role role = roleRepository.findByIdAndDeletedStatus(request.getRoleId(), 0)
                .orElseThrow(() -> new IllegalArgumentException("Role not found or deleted!"));

        // 2. Verify Menu exists and is active
        Menu menu = menuRepository.findByIdAndDeletedStatus(request.getMenuId(), 0)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found or deleted!"));

        // 3. Find existing mapping or create a new one
        Optional<RoleMenu> existingOpt = roleMenuRepository.findByRoleIdAndMenuIdAndDeletedStatus(
                request.getRoleId(), request.getMenuId(), 0);

        RoleMenu roleMenu;
        if (existingOpt.isPresent()) {
            roleMenu = existingOpt.get();
            roleMenu.setCanRead(request.getCanRead());
            roleMenu.setCanWrite(request.getCanWrite());
            roleMenu.setCanDelete(request.getCanDelete());
            roleMenu.setUpdatedBy(currentUsername != null ? currentUsername : "SYSTEM");
            roleMenu.setUpdatedAt(LocalDateTime.now());
        } else {
            roleMenu = RoleMenu.builder()
                    .roleId(request.getRoleId())
                    .menuId(request.getMenuId())
                    .canRead(request.getCanRead())
                    .canWrite(request.getCanWrite())
                    .canDelete(request.getCanDelete())
                    .build();
            roleMenu.setCreatedBy(currentUsername != null ? currentUsername : "SYSTEM");
            roleMenu.setStatus(1);
            roleMenu.setDeletedStatus(0);
        }

        RoleMenu saved = roleMenuRepository.save(roleMenu);

        // Notify all users who are assigned to this role of the permission update
        List<UserRole> userRoles = userRoleRepository.findAllByRoleIdAndDeletedStatus(request.getRoleId(), 0);
        String actionDetails = String.format("Read: %s, Write: %s, Delete: %s",
                request.getCanRead() == 1 ? "GRANTED" : "REVOKED",
                request.getCanWrite() == 1 ? "GRANTED" : "REVOKED",
                request.getCanDelete() == 1 ? "GRANTED" : "REVOKED");

        for (UserRole ur : userRoles) {
            userRepository.findById(ur.getUserId()).ifPresent(user -> {
                emailService.sendPermissionChangeNotification(
                        user.getEmail(),
                        user.getUsername(),
                        role.getName(),
                        menu.getName(),
                        actionDetails
                );
            });
        }

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleMenuResponse> getPermissionsByRoleId(Long roleId) {
        // Verify Role exists
        if (!roleRepository.existsById(roleId)) {
            throw new IllegalArgumentException("Role not found!");
        }
        return roleMenuRepository.findAllByRoleIdAndDeletedStatus(roleId, 0).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserPermissionResponse> getUserPermissions(String username) {
        // 1. Find user
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new IllegalArgumentException("User not found!"));

        // 2. Find all active user roles
        List<UserRole> userRoles = userRoleRepository.findAllByUserIdAndDeletedStatus(user.getId(), 0);
        if (userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. Retrieve and combine permissions from all roles
        Map<Long, UserPermissionResponse> combinedPermissions = new HashMap<>();

        for (UserRole ur : userRoles) {
            List<RoleMenu> roleMenus = roleMenuRepository.findAllByRoleIdAndDeletedStatus(ur.getRoleId(), 0);
            for (RoleMenu rm : roleMenus) {
                // Get menu details
                Menu menu = menuRepository.findByIdAndDeletedStatus(rm.getMenuId(), 0).orElse(null);
                if (menu == null) {
                    continue; // Skip if menu was soft deleted
                }

                UserPermissionResponse existing = combinedPermissions.get(rm.getMenuId());
                if (existing == null) {
                    existing = UserPermissionResponse.builder()
                            .menuId(menu.getId())
                            .menuName(menu.getName())
                            .menuCode(menu.getCode())
                            .menuPath(menu.getPath())
                            .canRead(rm.getCanRead())
                            .canWrite(rm.getCanWrite())
                            .canDelete(rm.getCanDelete())
                            .build();
                    combinedPermissions.put(rm.getMenuId(), existing);
                } else {
                    // Merge permissions (taking the highest privilege: Max value 1)
                    existing.setCanRead(Math.max(existing.getCanRead(), rm.getCanRead()));
                    existing.setCanWrite(Math.max(existing.getCanWrite(), rm.getCanWrite()));
                    existing.setCanDelete(Math.max(existing.getCanDelete(), rm.getCanDelete()));
                }
            }
        }

        return new ArrayList<>(combinedPermissions.values());
    }

    private RoleMenuResponse mapToResponse(RoleMenu roleMenu) {
        return RoleMenuResponse.builder()
                .id(roleMenu.getId())
                .roleId(roleMenu.getRoleId())
                .menuId(roleMenu.getMenuId())
                .canRead(roleMenu.getCanRead())
                .canWrite(roleMenu.getCanWrite())
                .canDelete(roleMenu.getCanDelete())
                .createdBy(roleMenu.getCreatedBy())
                .updatedBy(roleMenu.getUpdatedBy())
                .createdAt(roleMenu.getCreatedAt())
                .updatedAt(roleMenu.getUpdatedAt())
                .status(roleMenu.getStatus())
                .deletedStatus(roleMenu.getDeletedStatus())
                .build();
    }
}
