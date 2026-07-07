package com.hrms.service.impl;

import com.hrms.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

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
        String subject = "Verifikasi Alamat Email HRMS Anda";

        String htmlContent = String.format(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Verifikasi Akun HRMS</title>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;\n" +
            "            background-color: #f8fafc;\n" +
            "            margin: 0;\n" +
            "            padding: 0;\n" +
            "            -webkit-font-smoothing: antialiased;\n" +
            "        }\n" +
            "        .container {\n" +
            "            max-width: 600px;\n" +
            "            margin: 40px auto;\n" +
            "            background-color: #ffffff;\n" +
            "            border-radius: 16px;\n" +
            "            box-shadow: 0 4px 30px rgba(0, 0, 0, 0.03);\n" +
            "            overflow: hidden;\n" +
            "            border: 1px solid #e2e8f0;\n" +
            "        }\n" +
            "        .header {\n" +
            "            background: linear-gradient(135deg, #6366f1, #3b82f6);\n" +
            "            padding: 45px 20px;\n" +
            "            text-align: center;\n" +
            "            color: #ffffff;\n" +
            "        }\n" +
            "        .header h1 {\n" +
            "            margin: 0;\n" +
            "            font-size: 26px;\n" +
            "            font-weight: 700;\n" +
            "            letter-spacing: -0.5px;\n" +
            "        }\n" +
            "        .content {\n" +
            "            padding: 40px 30px;\n" +
            "            color: #334155;\n" +
            "            line-height: 1.6;\n" +
            "        }\n" +
            "        .content p {\n" +
            "            margin: 0 0 20px;\n" +
            "            font-size: 16px;\n" +
            "        }\n" +
            "        .cta-container {\n" +
            "            text-align: center;\n" +
            "            margin: 35px 0;\n" +
            "        }\n" +
            "        .btn {\n" +
            "            background-color: #4f46e5;\n" +
            "            color: #ffffff !important;\n" +
            "            text-decoration: none;\n" +
            "            padding: 14px 32px;\n" +
            "            border-radius: 8px;\n" +
            "            font-size: 16px;\n" +
            "            font-weight: 600;\n" +
            "            display: inline-block;\n" +
            "            box-shadow: 0 4px 12px rgba(79, 70, 229, 0.2);\n" +
            "        }\n" +
            "        .btn:hover {\n" +
            "            background-color: #4338ca;\n" +
            "        }\n" +
            "        .footer {\n" +
            "            background-color: #f8fafc;\n" +
            "            padding: 24px 30px;\n" +
            "            text-align: center;\n" +
            "            font-size: 13px;\n" +
            "            color: #64748b;\n" +
            "            border-top: 1px solid #f1f5f9;\n" +
            "        }\n" +
            "        .footer a {\n" +
            "            color: #4f46e5;\n" +
            "            text-decoration: none;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"header\">\n" +
            "            <h1>Selamat Datang di HRMS</h1>\n" +
            "        </div>\n" +
            "        <div class=\"content\">\n" +
            "            <p>Halo <strong>%s</strong>,</p>\n" +
            "            <p>Terima kasih telah bergabung dengan platform Human Resource Management System kami. Langkah kecil Anda hari ini adalah awal dari kolaborasi yang luar biasa.</p>\n" +
            "            <p>Untuk menyelesaikan pendaftaran dan mulai menjelajahi fitur-fitur kami, silakan konfirmasi alamat email Anda dengan menekan tombol di bawah ini:</p>\n" +
            "            <div class=\"cta-container\">\n" +
            "                <a href=\"%s\" class=\"btn\">Verifikasi Email Saya</a>\n" +
            "            </div>\n" +
            "            <p>Jika tombol di atas tidak berfungsi, Anda juga dapat menyalin dan menempel tautan berikut ke browser Anda:</p>\n" +
            "            <p style=\"word-break: break-all; font-size: 14px;\"><a href=\"%s\">%s</a></p>\n" +
            "            <p style=\"margin-top: 30px;\">Salam hangat,<br><strong>Tim HRMS</strong></p>\n" +
            "        </div>\n" +
            "        <div class=\"footer\">\n" +
            "            <p>Pesan ini dikirim secara otomatis oleh sistem keamanan kami. Jika Anda merasa tidak pernah melakukan pendaftaran ini, silakan abaikan email ini.</p>\n" +
            "            <p>&copy; 2026 HRMS. Semua hak cipta dilindungi.</p>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>",
            username, verificationUrl, verificationUrl, verificationUrl
        );

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void sendRoleAssignmentNotification(String toEmail, String username, List<String> roleNames) {
        String subject = "Penugasan Peran Baru Akun HRMS Anda";
        String listItemsHtml = roleNames.stream()
            .map(role -> "<li>" + role + "</li>")
            .collect(Collectors.joining("\n"));

        String htmlContent = String.format(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Pemberitahuan Peran Akun HRMS</title>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;\n" +
            "            background-color: #f8fafc;\n" +
            "            margin: 0;\n" +
            "            padding: 0;\n" +
            "            -webkit-font-smoothing: antialiased;\n" +
            "        }\n" +
            "        .container {\n" +
            "            max-width: 600px;\n" +
            "            margin: 40px auto;\n" +
            "            background-color: #ffffff;\n" +
            "            border-radius: 16px;\n" +
            "            box-shadow: 0 4px 30px rgba(0, 0, 0, 0.03);\n" +
            "            overflow: hidden;\n" +
            "            border: 1px solid #e2e8f0;\n" +
            "        }\n" +
            "        .header {\n" +
            "            background: linear-gradient(135deg, #10b981, #059669);\n" +
            "            padding: 45px 20px;\n" +
            "            text-align: center;\n" +
            "            color: #ffffff;\n" +
            "        }\n" +
            "        .header h1 {\n" +
            "            margin: 0;\n" +
            "            font-size: 26px;\n" +
            "            font-weight: 700;\n" +
            "            letter-spacing: -0.5px;\n" +
            "        }\n" +
            "        .content {\n" +
            "            padding: 40px 30px;\n" +
            "            color: #334155;\n" +
            "            line-height: 1.6;\n" +
            "        }\n" +
            "        .content p {\n" +
            "            margin: 0 0 20px;\n" +
            "            font-size: 16px;\n" +
            "        }\n" +
            "        .role-list {\n" +
            "            background-color: #f1f5f9;\n" +
            "            padding: 20px;\n" +
            "            border-radius: 12px;\n" +
            "            margin: 25px 0;\n" +
            "            border-left: 4px solid #10b981;\n" +
            "        }\n" +
            "        .role-list ul {\n" +
            "            margin: 0;\n" +
            "            padding-left: 20px;\n" +
            "        }\n" +
            "        .role-list li {\n" +
            "            font-size: 16px;\n" +
            "            font-weight: 600;\n" +
            "            color: #1e293b;\n" +
            "            margin-bottom: 8px;\n" +
            "        }\n" +
            "        .role-list li:last-child {\n" +
            "            margin-bottom: 0;\n" +
            "        }\n" +
            "        .footer {\n" +
            "            background-color: #f8fafc;\n" +
            "            padding: 24px 30px;\n" +
            "            text-align: center;\n" +
            "            font-size: 13px;\n" +
            "            color: #64748b;\n" +
            "            border-top: 1px solid #f1f5f9;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"header\">\n" +
            "            <h1>Pemberitahuan Peran Akun</h1>\n" +
            "        </div>\n" +
            "        <div class=\"content\">\n" +
            "            <p>Halo <strong>%s</strong>,</p>\n" +
            "            <p>Kami ingin menginformasikan bahwa akun Anda telah berhasil terdaftar dan diberikan hak peran (*role*) dalam sistem HRMS kami.</p>\n" +
            "            <p>Berikut adalah daftar peran yang saat ini ditugaskan kepada Anda:</p>\n" +
            "            <div class=\"role-list\">\n" +
            "                <ul>\n" +
            "                    %s\n" +
            "                </ul>\n" +
            "            </div>\n" +
            "            <p>Peran tersebut akan menentukan menu dan fitur kerja yang dapat Anda akses dalam platform kami. Jika Anda merasa ada kekeliruan dalam penugasan peran ini, silakan hubungi Administrator Sistem Anda.</p>\n" +
            "            <p style=\"margin-top: 30px;\">Salam hangat,<br><strong>Tim HRMS</strong></p>\n" +
            "        </div>\n" +
            "        <div class=\"footer\">\n" +
            "            <p>Pesan ini dikirim secara otomatis oleh sistem administrasi kami. Mohon tidak membalas email ini.</p>\n" +
            "            <p>&copy; 2026 HRMS. Semua hak cipta dilindungi.</p>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>",
            username, listItemsHtml
        );

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    @Override
    public void sendPermissionChangeNotification(String toEmail, String username, String roleName, String menuName, String actionDetails) {
        String subject = "Pembaruan Izin & Hak Akses Akun HRMS Anda";

        String htmlContent = String.format(
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <title>Pemberitahuan Perubahan Hak Akses HRMS</title>\n" +
            "    <style>\n" +
            "        body {\n" +
            "            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;\n" +
            "            background-color: #f8fafc;\n" +
            "            margin: 0;\n" +
            "            padding: 0;\n" +
            "            -webkit-font-smoothing: antialiased;\n" +
            "        }\n" +
            "        .container {\n" +
            "            max-width: 600px;\n" +
            "            margin: 40px auto;\n" +
            "            background-color: #ffffff;\n" +
            "            border-radius: 16px;\n" +
            "            box-shadow: 0 4px 30px rgba(0, 0, 0, 0.03);\n" +
            "            overflow: hidden;\n" +
            "            border: 1px solid #e2e8f0;\n" +
            "        }\n" +
            "        .header {\n" +
            "            background: linear-gradient(135deg, #f59e0b, #d97706);\n" +
            "            padding: 45px 20px;\n" +
            "            text-align: center;\n" +
            "            color: #ffffff;\n" +
            "        }\n" +
            "        .header h1 {\n" +
            "            margin: 0;\n" +
            "            font-size: 26px;\n" +
            "            font-weight: 700;\n" +
            "            letter-spacing: -0.5px;\n" +
            "        }\n" +
            "        .content {\n" +
            "            padding: 40px 30px;\n" +
            "            color: #334155;\n" +
            "            line-height: 1.6;\n" +
            "        }\n" +
            "        .content p {\n" +
            "            margin: 0 0 20px;\n" +
            "            font-size: 16px;\n" +
            "        }\n" +
            "        .details-box {\n" +
            "            background-color: #fffbeb;\n" +
            "            padding: 20px;\n" +
            "            border-radius: 12px;\n" +
            "            margin: 25px 0;\n" +
            "            border-left: 4px solid #f59e0b;\n" +
            "        }\n" +
            "        .details-box table {\n" +
            "            width: 100%;\n" +
            "            border-collapse: collapse;\n" +
            "        }\n" +
            "        .details-box td {\n" +
            "            padding: 6px 0;\n" +
            "            font-size: 15px;\n" +
            "        }\n" +
            "        .details-box td.label {\n" +
            "            font-weight: 600;\n" +
            "            color: #451a03;\n" +
            "            width: 35%;\n" +
            "        }\n" +
            "        .details-box td.value {\n" +
            "            color: #78350f;\n" +
            "        }\n" +
            "        .footer {\n" +
            "            background-color: #f8fafc;\n" +
            "            padding: 24px 30px;\n" +
            "            text-align: center;\n" +
            "            font-size: 13px;\n" +
            "            color: #64748b;\n" +
            "            border-top: 1px solid #f1f5f9;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div class=\"container\">\n" +
            "        <div class=\"header\">\n" +
            "            <h1>Pembaruan Hak Akses</h1>\n" +
            "        </div>\n" +
            "        <div class=\"content\">\n" +
            "            <p>Halo <strong>%s</strong>,</p>\n" +
            "            <p>Kami ingin memberi tahu Anda bahwa telah terjadi perubahan atau pembaruan hak akses menu untuk salah satu peran yang terkait dengan akun Anda.</p>\n" +
            "            <p>Berikut adalah detail pembaruan akses terbaru Anda:</p>\n" +
            "            <div class=\"details-box\">\n" +
            "                <table>\n" +
            "                    <tr>\n" +
            "                        <td class=\"label\">Nama Peran:</td>\n" +
            "                        <td class=\"value\">%s</td>\n" +
            "                    </tr>\n" +
            "                    <tr>\n" +
            "                        <td class=\"label\">Nama Menu:</td>\n" +
            "                        <td class=\"value\">%s</td>\n" +
            "                    </tr>\n" +
            "                    <tr>\n" +
            "                        <td class=\"label\">Detail Izin:</td>\n" +
            "                        <td class=\"value\">%s</td>\n" +
            "                    </tr>\n" +
            "                </table>\n" +
            "            </div>\n" +
            "            <p>Perubahan ini telah diterapkan langsung dalam sistem. Anda dapat memuat ulang halaman HRMS Anda untuk melihat pembaruan akses ini.</p>\n" +
            "            <p style=\"margin-top: 30px;\">Salam hangat,<br><strong>Tim HRMS</strong></p>\n" +
            "        </div>\n" +
            "        <div class=\"footer\">\n" +
            "            <p>Pesan ini dikirim secara otomatis oleh sistem administrasi keamanan kami. Mohon tidak membalas email ini.</p>\n" +
            "            <p>&copy; 2026 HRMS. Semua hak cipta dilindungi.</p>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</body>\n" +
            "</html>",
            username, roleName, menuName, actionDetails
        );

        sendHtmlEmail(toEmail, subject, htmlContent);
    }

    private void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("HTML Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send HTML email to {}: {}", to, e.getMessage());
            // Do not block execution/registration flows if SMTP is unconfigured or failing in development.
        }
    }
}
