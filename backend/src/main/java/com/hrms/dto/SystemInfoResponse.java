package com.hrms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemInfoResponse {
    private String osName;
    private String osVersion;
    private String osArch;
    private String jvmVersion;
    private String jvmVendor;
    private int cpuCores;
    private long totalMemoryBytes;
    private long freeMemoryBytes;
    private long maxMemoryBytes;
    private int activeThreads;
    private String dbStatus;
    private long activeUsersCount;
    private long activeRolesCount;
    private long activeEmployeesCount;
    private long jwtExpirationMs;
    private String serverPort;
}
