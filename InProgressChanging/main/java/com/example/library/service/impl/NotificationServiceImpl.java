package com.example.library.service.impl;

import com.example.library.entity.BorrowRecord;
import com.example.library.entity.Notification;
import com.example.library.entity.enums.NotificationType;
import com.example.library.exception.BusinessException;
import com.example.library.exception.ErrorResponse;
import com.example.library.repository.NotificationRepository;
import com.example.library.service.base.INotificationService;
import com.example.library.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public Notification createNotification(BorrowRecord borrowRecord, NotificationType type, String message) {
        Notification notification = Notification.builder()
                .borrowRecord(borrowRecord)
                .user(borrowRecord.getBorrower())
                .type(type)
                .message(message)
                .isRead(false)
                .sentAt(TimeUtils.now())
                .build();
        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsForUser(Long userId) {
        return notificationRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));

        if (!notification.getUser().getUserId().equals(userId)) {
            throw new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND);
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }
}
