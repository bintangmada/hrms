package com.hrms.service.impl;

import com.hrms.dto.MenuRequest;
import com.hrms.dto.MenuResponse;
import com.hrms.entity.Menu;
import com.hrms.repository.MenuRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MenuServiceImplTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuServiceImpl menuService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateMenu_Success() {
        MenuRequest request = MenuRequest.builder()
                .name("Finance Management")
                .code("FINANCE")
                .path("/finance")
                .build();

        Menu mockSaved = Menu.builder()
                .id(1L)
                .name("Finance Management")
                .code("FINANCE")
                .path("/finance")
                .build();
        mockSaved.setCreatedBy("admin");
        mockSaved.setStatus(1);
        mockSaved.setDeletedStatus(0);

        when(menuRepository.existsByCodeAndDeletedStatus("FINANCE", 0)).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenReturn(mockSaved);

        MenuResponse response = menuService.createMenu(request, "admin");

        assertNotNull(response);
        assertEquals("Finance Management", response.getName());
        assertEquals("FINANCE", response.getCode());
        assertEquals("/finance", response.getPath());
        verify(menuRepository, times(1)).save(any(Menu.class));
    }

    @Test
    public void testCreateMenu_DuplicateCode() {
        MenuRequest request = MenuRequest.builder()
                .name("Finance")
                .code("FINANCE")
                .path("/finance")
                .build();

        when(menuRepository.existsByCodeAndDeletedStatus("FINANCE", 0)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> menuService.createMenu(request, "admin"));
    }

    @Test
    public void testCreateMenu_Placeholder() {
        MenuRequest request = MenuRequest.builder()
                .name("string")
                .code("FINANCE")
                .path("/finance")
                .build();

        assertThrows(IllegalArgumentException.class, () -> menuService.createMenu(request, "admin"));
    }

    @Test
    public void testUpdateMenu_Success() {
        MenuRequest request = MenuRequest.builder()
                .name("New Finance Name")
                .code("FINANCE")
                .path("/new-finance")
                .build();

        Menu existing = Menu.builder()
                .id(1L)
                .name("Finance Management")
                .code("FINANCE")
                .path("/finance")
                .build();

        Menu updated = Menu.builder()
                .id(1L)
                .name("New Finance Name")
                .code("FINANCE")
                .path("/new-finance")
                .build();

        when(menuRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.of(existing));
        when(menuRepository.existsByCodeAndDeletedStatus("FINANCE", 0)).thenReturn(false);
        when(menuRepository.save(any(Menu.class))).thenReturn(updated);

        MenuResponse response = menuService.updateMenu(1L, request, "admin");

        assertNotNull(response);
        assertEquals("New Finance Name", response.getName());
        assertEquals("/new-finance", response.getPath());
    }

    @Test
    public void testUpdateMenu_NotFound() {
        MenuRequest request = MenuRequest.builder()
                .name("Finance")
                .code("FINANCE")
                .path("/finance")
                .build();

        when(menuRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> menuService.updateMenu(1L, request, "admin"));
    }

    @Test
    public void testDeleteMenu_Success() {
        Menu existing = Menu.builder().id(1L).name("Finance").build();
        when(menuRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.of(existing));

        String result = menuService.deleteMenu(1L, "admin");

        assertEquals("Menu deleted successfully!", result);
        assertEquals(1, existing.getDeletedStatus());
        assertEquals("admin", existing.getDeletedBy());
        verify(menuRepository, times(1)).save(existing);
    }

    @Test
    public void testGetMenuById_Success() {
        Menu menu = Menu.builder().id(1L).name("Finance").build();
        when(menuRepository.findByIdAndDeletedStatus(1L, 0)).thenReturn(Optional.of(menu));

        MenuResponse response = menuService.getMenuById(1L);

        assertNotNull(response);
        assertEquals("Finance", response.getName());
    }

    @Test
    public void testGetAllActiveMenus() {
        Menu menu1 = Menu.builder().id(1L).name("Finance").build();
        Menu menu2 = Menu.builder().id(2L).name("Attendance").build();
        when(menuRepository.findAllByDeletedStatus(0)).thenReturn(List.of(menu1, menu2));

        List<MenuResponse> list = menuService.getAllActiveMenus();

        assertEquals(2, list.size());
    }
}
