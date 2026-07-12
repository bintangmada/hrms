package com.hrms.controller;

import com.hrms.dto.ApiResponse;
import com.hrms.dto.SystemInfoResponse;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.RoleRepository;
import com.hrms.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
@Tag(name = "System Diagnostics", description = "Endpoints for retrieving system metrics and configs (Super Admin only)")
public class MasterController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmployeeRepository employeeRepository;

    @Value("${app.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    @Value("${server.port:8020}")
    private String serverPort;

    @Operation(summary = "Get system diagnostics & metrics (Super Admin only)")
    @GetMapping("/info")
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<SystemInfoResponse>> getSystemInfo() {
        Runtime runtime = Runtime.getRuntime();
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        // Check DB Status
        String dbStatus = "CONNECTED";
        long usersCount = 0;
        long rolesCount = 0;
        long employeesCount = 0;

        try {
            usersCount = userRepository.count();
            rolesCount = roleRepository.count();
            employeesCount = employeeRepository.count();
        } catch (Exception e) {
            dbStatus = "DISCONNECTED (" + e.getMessage() + ")";
        }

        SystemInfoResponse info = SystemInfoResponse.builder()
                .osName(System.getProperty("os.name"))
                .osVersion(System.getProperty("os.version"))
                .osArch(System.getProperty("os.arch"))
                .jvmVersion(System.getProperty("java.version"))
                .jvmVendor(System.getProperty("java.vendor"))
                .cpuCores(runtime.availableProcessors())
                .totalMemoryBytes(runtime.totalMemory())
                .freeMemoryBytes(runtime.freeMemory())
                .maxMemoryBytes(runtime.maxMemory())
                .activeThreads(threadMXBean.getThreadCount())
                .dbStatus(dbStatus)
                .activeUsersCount(usersCount)
                .activeRolesCount(rolesCount)
                .activeEmployeesCount(employeesCount)
                .jwtExpirationMs(jwtExpirationMs)
                .serverPort(serverPort)
                .build();

        return ResponseEntity.ok(ApiResponse.success("System info retrieved successfully", info));
    }
}
