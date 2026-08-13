package com.example.library.service.base;

import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Notification;
import com.example.library.entity.enums.NotificationType;

import java.util.List;

public interface INotificationService {
    Notification createNotification(BorrowRecord borrowRecord, NotificationType type, String message);
    List<Notification> getNotificationsForUser(Long userId);
    void markAsRead(Long notificationId, Long userId);
}
