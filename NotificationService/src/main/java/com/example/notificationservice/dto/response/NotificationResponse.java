package com.example.notificationservice.dto.response;

import com.example.notificationservice.entity.enums.NotificationType;

import java.time.OffsetDateTime;

public record NotificationResponse(
        Long notificationId,
        Long userId,
        Long borrowRecordId,
        NotificationType type,
        String message,
        Boolean isRead,
        OffsetDateTime sentAt,
        OffsetDateTime createdAt
) {
}
