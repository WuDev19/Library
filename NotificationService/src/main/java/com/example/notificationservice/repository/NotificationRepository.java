package com.example.notificationservice.repository;

import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.entity.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByBorrowRecordIdAndType(Long borrowRecordId, NotificationType type);
}
