package com.example.notificationservice.service.base;

import com.example.notificationservice.dto.event.NotificationEventPayload;

public interface IEmailService {
    void sendNotificationEmail(String recipientEmail, String recipientName, NotificationEventPayload payload);
}
