package com.hrms.service.impl;

import com.hrms.dto.MenuRequest;
import com.hrms.dto.MenuResponse;
import com.hrms.entity.Menu;
import com.hrms.repository.MenuRepository;
import com.hrms.service.MenuService;
import com.hrms.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;

    @Override
    @Transactional
    public MenuResponse createMenu(MenuRequest request, String currentUsername) {
        ValidationUtils.validateNotPlaceholder(request.getName(), "Menu name");
        ValidationUtils.validateNotPlaceholder(request.getCode(), "Menu code");
        ValidationUtils.validateNotPlaceholder(request.getPath(), "Menu path");

        String code = request.getCode().trim().toUpperCase();

        if (menuRepository.existsByCodeAndDeletedStatus(code, 0)) {
            throw new IllegalArgumentException("Menu code already exists!");
        }

        Menu menu = Menu.builder()
                .name(request.getName().trim())
                .code(code)
                .path(request.getPath().trim())
                .build();

        menu.setCreatedBy(currentUsername != null ? currentUsername : "SYSTEM");
        menu.setStatus(1);
        menu.setDeletedStatus(0);

        Menu saved = menuRepository.save(menu);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public MenuResponse updateMenu(Long id, MenuRequest request, String currentUsername) {
        ValidationUtils.validateNotPlaceholder(request.getName(), "Menu name");
        ValidationUtils.validateNotPlaceholder(request.getCode(), "Menu code");
        ValidationUtils.validateNotPlaceholder(request.getPath(), "Menu path");

        Menu menu = menuRepository.findByIdAndDeletedStatus(id, 0)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found or deleted!"));

        String code = request.getCode().trim().toUpperCase();

        if (!menu.getCode().equals(code) && menuRepository.existsByCodeAndDeletedStatus(code, 0)) {
            throw new IllegalArgumentException("Menu code already exists!");
        }

        menu.setName(request.getName().trim());
        menu.setCode(code);
        menu.setPath(request.getPath().trim());
        menu.setUpdatedBy(currentUsername != null ? currentUsername : "SYSTEM");
        menu.setUpdatedAt(LocalDateTime.now());

        Menu updated = menuRepository.save(menu);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public String deleteMenu(Long id, String currentUsername) {
        Menu menu = menuRepository.findByIdAndDeletedStatus(id, 0)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found or deleted!"));

        menu.setDeletedStatus(1);
        menu.setDeletedBy(currentUsername != null ? currentUsername : "SYSTEM");
        menu.setDeletedAt(LocalDateTime.now());

        menuRepository.save(menu);
        return "Menu deleted successfully!";
    }

    @Override
    @Transactional(readOnly = true)
    public MenuResponse getMenuById(Long id) {
        Menu menu = menuRepository.findByIdAndDeletedStatus(id, 0)
                .orElseThrow(() -> new IllegalArgumentException("Menu not found or deleted!"));
        return mapToResponse(menu);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuResponse> getAllActiveMenus() {
        return menuRepository.findAllByDeletedStatus(0).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private MenuResponse mapToResponse(Menu menu) {
        return MenuResponse.builder()
                .id(menu.getId())
                .name(menu.getName())
                .code(menu.getCode())
                .path(menu.getPath())
                .createdBy(menu.getCreatedBy())
                .updatedBy(menu.getUpdatedBy())
                .createdAt(menu.getCreatedAt())
                .updatedAt(menu.getUpdatedAt())
                .status(menu.getStatus())
                .deletedStatus(menu.getDeletedStatus())
                .build();
    }
}
