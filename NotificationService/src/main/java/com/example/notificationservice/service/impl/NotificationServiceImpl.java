package com.example.notificationservice.service.impl;

import com.example.notificationservice.dto.response.NotificationResponse;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.exception.BusinessException;
import com.example.notificationservice.exception.ErrorResponse;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.base.INotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(n -> new NotificationResponse(
                        n.getNotificationId(),
                        n.getUserId(),
                        n.getBorrowRecordId(),
                        n.getType(),
                        n.getMessage(),
                        n.getIsRead(),
                        n.getSentAt(),
                        n.getCreatedAt()
                ))
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND);
        }
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}
