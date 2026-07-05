package com.hrms.service.impl;

import com.hrms.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.base-url:http://localhost:8020}")
    private String baseUrl;

    @Value("${spring.mail.username:noreply@hrms.com}")
    private String fromEmail;

    @Override
    public void sendVerificationEmail(String toEmail, String username, String token) {
        String verificationUrl = baseUrl + "/api/v1/auth/confirm-email?token=" + token;
        String subject = "Verify Your HRMS Account Email";
        String content = "Hello " + username + ",\n\n"
                + "Thank you for registering with HRMS! Please click the link below to verify your email address:\n"
                + verificationUrl + "\n\n"
                + "If you did not initiate this request, please ignore this email.\n\n"
                + "Best Regards,\nHRMS Team";

        sendEmail(toEmail, subject, content);
    }

    @Override
    public void sendRoleAssignmentNotification(String toEmail, String username, List<String> roleNames) {
        String subject = "HRMS Account Role Assignment Update";
        String content = "Hello " + username + ",\n\n"
                + "Your account has been assigned the following roles:\n"
                + String.join(", ", roleNames) + "\n\n"
                + "If you have any questions, please contact your system administrator.\n\n"
                + "Best Regards,\nHRMS Team";

        sendEmail(toEmail, subject, content);
    }

    @Override
    public void sendPermissionChangeNotification(String toEmail, String username, String roleName, String menuName, String actionDetails) {
        String subject = "HRMS Permission Update for Role: " + roleName;
        String content = "Hello " + username + ",\n\n"
                + "This is to inform you that your role '" + roleName + "' has received a permission update on menu '" + menuName + "':\n"
                + actionDetails + "\n\n"
                + "Best Regards,\nHRMS Team";

        sendEmail(toEmail, subject, content);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            // Do not block execution/registration flows if SMTP is unconfigured or failing in development.
        }
    }
}
