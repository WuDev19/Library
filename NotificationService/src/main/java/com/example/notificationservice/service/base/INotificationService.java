package com.example.notificationservice.service.base;

import com.example.notificationservice.dto.response.NotificationResponse;

import java.util.List;

public interface INotificationService {

    List<NotificationResponse> getNotificationsForUser(Long userId);

    void markAsRead(Long notificationId, Long userId);
}
