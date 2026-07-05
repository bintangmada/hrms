package com.hrms.service.impl;

import com.hrms.dto.RoleRequest;
import com.hrms.dto.RoleResponse;
import com.hrms.entity.Role;
import com.hrms.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateRole_Success() {
        RoleRequest request = RoleRequest.builder()
                .name("HR_STAFF")
                .description("HR Staff role description")
                .build();

        Role mockSavedRole = Role.builder()
                .id(1L)
                .name("ROLE_HR_STAFF")
                .description("HR Staff role description")
                .build();
        mockSavedRole.setCreatedBy("admin");
        mockSavedRole.setStatus(1);
        mockSavedRole.setDeletedStatus(0);

        when(roleRepository.existsByNameAndDeletedStatus("ROLE_HR_STAFF", 0)).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(mockSavedRole);

        RoleResponse response = roleService.createRole(request, "admin");

        assertNotNull(response);
        assertEquals("ROLE_HR_STAFF", response.getName());
        assertEquals("HR Staff role description", response.getDescription());
        assertEquals(0, response.getDeletedStatus());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    public void testCreateRole_DuplicateName() {
        RoleRequest request = RoleRequest.builder()
                .name("ROLE_HR_STAFF")
                .build();

        when(roleRepository.existsByNameAndDeletedStatus("ROLE_HR_STAFF", 0)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> roleService.createRole(request, "admin"));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testCreateRole_PlaceholderName() {
        RoleRequest request = RoleRequest.builder()
                .name("string")
                .build();

        assertThrows(IllegalArgumentException.class, () -> roleService.createRole(request, "admin"));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    public void testUpdateRole_Success() {
        RoleRequest request = RoleRequest.builder()
                .name("ROLE_MANAGER")
                .description("Updated manager description")
                .build();

        Role existingRole = Role.builder()
                .id(2L)
                .name("ROLE_STAFF")
                .description("Old description")
                .build();

        Role updatedRole = Role.builder()
                .id(2L)
                .name("ROLE_MANAGER")
                .description("Updated manager description")
                .build();

        when(roleRepository.findByIdAndDeletedStatus(2L, 0)).thenReturn(Optional.of(existingRole));
        when(roleRepository.existsByNameAndDeletedStatus("ROLE_MANAGER", 0)).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(updatedRole);

        RoleResponse response = roleService.updateRole(2L, request, "admin");

        assertNotNull(response);
        assertEquals("ROLE_MANAGER", response.getName());
        assertEquals("Updated manager description", response.getDescription());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    public void testUpdateRole_NotFound() {
        RoleRequest request = RoleRequest.builder().name("ROLE_ADMIN").build();
        when(roleRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> roleService.updateRole(1L, request, "admin"));
    }

    @Test
    public void testDeleteRole_Success() {
        Role existingRole = Role.builder().id(3L).name("ROLE_TEMP").build();
        when(roleRepository.findByIdAndDeletedStatus(3L, 0)).thenReturn(Optional.of(existingRole));

        String message = roleService.deleteRole(3L, "admin");

        assertEquals("Role deleted successfully!", message);
        assertEquals(1, existingRole.getDeletedStatus());
        assertEquals("admin", existingRole.getDeletedBy());
        verify(roleRepository, times(1)).save(existingRole);
    }

    @Test
    public void testGetRoleById_Success() {
        Role role = Role.builder().id(10L).name("ROLE_SOME").build();
        when(roleRepository.findByIdAndDeletedStatus(10L, 0)).thenReturn(Optional.of(role));

        RoleResponse response = roleService.getRoleById(10L);

        assertNotNull(response);
        assertEquals("ROLE_SOME", response.getName());
    }

    @Test
    public void testGetAllActiveRoles() {
        Role role1 = Role.builder().id(1L).name("ROLE_A").build();
        Role role2 = Role.builder().id(2L).name("ROLE_B").build();
        when(roleRepository.findAllByDeletedStatus(0)).thenReturn(List.of(role1, role2));

        List<RoleResponse> list = roleService.getAllActiveRoles();

        assertEquals(2, list.size());
        assertEquals("ROLE_A", list.get(0).getName());
    }
}
