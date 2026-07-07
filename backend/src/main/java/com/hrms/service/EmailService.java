package com.hrms.service;

import java.util.List;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String username, String token);
    void sendRoleAssignmentNotification(String toEmail, String username, List<String> roleNames);
    void sendPermissionChangeNotification(String toEmail, String username, String roleName, String menuName, String actionDetails);
}
