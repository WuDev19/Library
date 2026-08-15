package com.example.notificationservice.service.impl;

import com.example.notificationservice.dto.event.NotificationEventPayload;
import com.example.notificationservice.service.base.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements IEmailService {

    private final JavaMailSender mailSender;

    @Value("${APP_EMAIL}")
    private String fromEmail;

    @Override
    @Async
    public void sendNotificationEmail(String recipientEmail, String recipientName, NotificationEventPayload payload) {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            log.warn("[Email Skip] Email người nhận trống cho userId={}", payload.userId());
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = getMimeMessageHelper(recipientEmail, payload, message);
            String htmlContent = buildEmailHtml(recipientName, payload);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("[Email Sent FAILED] Lỗi khi gửi email tới {}: {}", recipientEmail, e.getMessage(), e);
        }
    }

    private @NonNull MimeMessageHelper getMimeMessageHelper(String recipientEmail, NotificationEventPayload payload, MimeMessage message) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail);
        helper.setTo(recipientEmail);
        String subject;
        switch (payload.type()) {
            case OVERDUE -> subject = "[THƯ VIỆN] Thông Báo Sách Quá Hạn Trả - " + payload.bookTitle();
            case DUE_SOON -> subject = "[THƯ VIỆN] Nhắc Nhở Sắp Đến Hạn Trả Sách - " + payload.bookTitle();
            default -> subject = "[THƯ VIỆN] Thông Tin Sách Đang Mượn - " + payload.bookTitle();
        }
        helper.setSubject(subject);
        return helper;
    }

    private String buildEmailHtml(String recipientName, NotificationEventPayload payload) {
        String nameStr = (recipientName != null && !recipientName.isBlank()) ? recipientName : "Độc giả";
        String color = switch (payload.type()) {
            case OVERDUE -> "#dc3545";
            case DUE_SOON -> "#ffc107";
            default -> "#0d6efd";
        };
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; background-color: #f4f6f9; margin: 0; padding: 20px; }
                        .container { max-width: 600px; background: #ffffff; padding: 30px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
                        .header { background: %s; color: #ffffff; padding: 15px; border-radius: 6px 6px 0 0; text-align: center; font-size: 20px; font-weight: bold; }
                        .content { padding: 20px; line-height: 1.6; color: #333333; }
                        .footer { margin-top: 20px; text-align: center; font-size: 12px; color: #888888; border-top: 1px solid #eeeeee; padding-top: 10px; }
                        .info-table { width: 100%%; margin-top: 15px; border-collapse: collapse; }
                        .info-table td { padding: 8px; border-bottom: 1px solid #eeeeee; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">THÔNG BÁO THƯ VIỆN</div>
                        <div class="content">
                            <p>Xin chào <strong>%s</strong>,</p>
                            <p>%s</p>
                            <table class="info-table">
                                <tr><td><strong>Tên sách:</strong></td><td>%s</td></tr>
                                <tr><td><strong>Mã mượn:</strong></td><td>%s</td></tr>
                                <tr><td><strong>Hạn trả:</strong></td><td>%s</td></tr>
                            </table>
                            <p style="margin-top: 20px;">Vui lòng liên hệ bộ phận Thư viện nếu bạn cần hỗ trợ thêm.</p>
                        </div>
                        <div class="footer">
                            <p>Email này được gửi tự động từ Hệ thống Quản lý Thư viện.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(
                color,
                nameStr,
                payload.message(),
                payload.bookTitle(),
                payload.borrowCode(),
                payload.dueDate() != null ? payload.dueDate().toString() : "N/A"
        );
    }
}
