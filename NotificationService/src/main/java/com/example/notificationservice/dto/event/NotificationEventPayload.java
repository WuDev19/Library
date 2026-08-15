package com.example.notificationservice.dto.event;

import com.example.notificationservice.entity.enums.NotificationType;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record NotificationEventPayload(
        Long userId,
        Long borrowRecordId,
        String borrowCode,
        String bookTitle,
        NotificationType type,
        String message,
        LocalDate dueDate,
        OffsetDateTime createdAt
) {
}
