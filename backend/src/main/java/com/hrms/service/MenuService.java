package com.hrms.service;

import com.hrms.dto.MenuRequest;
import com.hrms.dto.MenuResponse;
import java.util.List;

public interface MenuService {
    MenuResponse createMenu(MenuRequest request, String currentUsername);
    MenuResponse updateMenu(Long id, MenuRequest request, String currentUsername);
    String deleteMenu(Long id, String currentUsername);
    MenuResponse getMenuById(Long id);
    List<MenuResponse> getAllActiveMenus();
}
