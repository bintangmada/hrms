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

public class RoleMenuServiceImplTest {

    @Mock
    private RoleMenuRepository roleMenuRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private RoleMenuServiceImpl roleMenuService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAssignPermission_NewSuccess() {
        RoleMenuRequest request = RoleMenuRequest.builder()
                .roleId(1L)
                .menuId(2L)
                .canRead(1)
                .canWrite(1)
                .canDelete(0)
                .build();

        Role mockRole = Role.builder().id(1L).name("ROLE_ADMIN").build();
        Menu mockMenu = Menu.builder().id(2L).name("Finance").build();
        RoleMenu mockSaved = RoleMenu.builder()
                .id(10L)
                .roleId(1L)
                .menuId(2L)
                .canRead(1)
                .canWrite(1)
                .canDelete(0)
                .build();

        when(roleRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.of(mockRole));
        when(menuRepository.findByIdAndDeletedStatus(2L, 0)).thenReturn(Optional.of(mockMenu));
        when(roleMenuRepository.findByRoleIdAndMenuIdAndDeletedStatus(1L, 2L, 0)).thenReturn(Optional.empty());
        when(roleMenuRepository.save(any(RoleMenu.class))).thenReturn(mockSaved);

        RoleMenuResponse response = roleMenuService.assignPermission(request, "admin");

        assertNotNull(response);
        assertEquals(1L, response.getRoleId());
        assertEquals(2L, response.getMenuId());
        assertEquals(1, response.getCanRead());
        assertEquals(1, response.getCanWrite());
        assertEquals(0, response.getCanDelete());
    }

    @Test
    public void testAssignPermission_UpdateSuccess() {
        RoleMenuRequest request = RoleMenuRequest.builder()
                .roleId(1L)
                .menuId(2L)
                .canRead(1)
                .canWrite(0)
                .canDelete(0)
                .build();

        Role mockRole = Role.builder().id(1L).name("ROLE_ADMIN").build();
        Menu mockMenu = Menu.builder().id(2L).name("Finance").build();
        RoleMenu existing = RoleMenu.builder()
                .id(10L)
                .roleId(1L)
                .menuId(2L)
                .canRead(1)
                .canWrite(1)
                .canDelete(1)
                .build();

        when(roleRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.of(mockRole));
        when(menuRepository.findByIdAndDeletedStatus(2L, 0)).thenReturn(Optional.of(mockMenu));
        when(roleMenuRepository.findByRoleIdAndMenuIdAndDeletedStatus(1L, 2L, 0)).thenReturn(Optional.of(existing));
        when(roleMenuRepository.save(any(RoleMenu.class))).thenReturn(existing);

        RoleMenuResponse response = roleMenuService.assignPermission(request, "admin");

        assertNotNull(response);
        assertEquals(0, response.getCanWrite());
        assertEquals(0, response.getCanDelete());
    }

    @Test
    public void testAssignPermission_RoleNotFound() {
        RoleMenuRequest request = RoleMenuRequest.builder().roleId(1L).menuId(2L).build();
        when(roleRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> roleMenuService.assignPermission(request, "admin"));
    }

    @Test
    public void testAssignPermission_MenuNotFound() {
        RoleMenuRequest request = RoleMenuRequest.builder().roleId(1L).menuId(2L).build();
        Role mockRole = Role.builder().id(1L).build();
        when(roleRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.of(mockRole));
        when(menuRepository.findByIdAndDeletedStatus(2L, 0)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> roleMenuService.assignPermission(request, "admin"));
    }

    @Test
    public void testGetPermissionsByRoleId_Success() {
        RoleMenu rm = RoleMenu.builder().id(1L).roleId(10L).menuId(2L).build();
        when(roleRepository.existsById(10L)).thenReturn(true);
        when(roleMenuRepository.findAllByRoleIdAndDeletedStatus(10L, 0)).thenReturn(List.of(rm));

        List<RoleMenuResponse> response = roleMenuService.getPermissionsByRoleId(10L);

        assertEquals(1, response.size());
    }

    @Test
    public void testGetPermissionsByRoleId_RoleNotFound() {
        when(roleRepository.existsById(10L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> roleMenuService.getPermissionsByRoleId(10L));
    }

    @Test
    public void testGetUserPermissions_Success() {
        User user = User.builder().id(100L).username("test_user").build();
        UserRole ur1 = UserRole.builder().userId(100L).roleId(1L).build();
        UserRole ur2 = UserRole.builder().userId(100L).roleId(2L).build();

        RoleMenu rm1 = RoleMenu.builder().roleId(1L).menuId(5L).canRead(1).canWrite(0).canDelete(0).build();
        RoleMenu rm2 = RoleMenu.builder().roleId(2L).menuId(5L).canRead(1).canWrite(1).canDelete(0).build();

        Menu menu = Menu.builder().id(5L).name("Attendance").code("ATT").path("/att").build();

        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(user));
        when(userRoleRepository.findAllByUserIdAndDeletedStatus(100L, 0)).thenReturn(List.of(ur1, ur2));
        when(roleMenuRepository.findAllByRoleIdAndDeletedStatus(1L, 0)).thenReturn(List.of(rm1));
        when(roleMenuRepository.findAllByRoleIdAndDeletedStatus(2L, 0)).thenReturn(List.of(rm2));
        when(menuRepository.findByIdAndDeletedStatus(5L, 0)).thenReturn(Optional.of(menu));

        List<UserPermissionResponse> response = roleMenuService.getUserPermissions("test_user");

        assertEquals(1, response.size());
        UserPermissionResponse up = response.get(0);
        assertEquals(5L, up.getMenuId());
        assertEquals("Attendance", up.getMenuName());
        assertEquals(1, up.getCanRead());
        assertEquals(1, up.getCanWrite());
        assertEquals(0, up.getCanDelete());
    }

    @Test
    public void testGetUserPermissions_NoRoles() {
        User user = User.builder().id(100L).username("test_user").build();
        when(userRepository.findByUsername("test_user")).thenReturn(Optional.of(user));
        when(userRoleRepository.findAllByUserIdAndDeletedStatus(100L, 0)).thenReturn(Collections.emptyList());

        List<UserPermissionResponse> response = roleMenuService.getUserPermissions("test_user");

        assertTrue(response.isEmpty());
    }
}
