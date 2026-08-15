package com.example.notificationservice.listener;

import com.example.notificationservice.client.UserServiceClient;
import com.example.notificationservice.dto.common.ApiResult;
import com.example.notificationservice.dto.event.NotificationEventPayload;
import com.example.notificationservice.dto.response.UserResponse;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.impl.EmailServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaListener {

    private final NotificationRepository notificationRepository;
    private final UserServiceClient userServiceClient;
    private final EmailServiceImpl emailServiceImpl;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    @Transactional
    public void handleNotificationEvent(String messagePayload) {

        NotificationEventPayload payload;
        try {
            payload = objectMapper.readValue(messagePayload, NotificationEventPayload.class);
        } catch (Exception e) {
            return;
        }
        boolean exists = notificationRepository.existsByBorrowRecordIdAndType(payload.borrowRecordId(), payload.type());
        if (exists) {
            return;
        }
        Notification notification = Notification.builder()
                .userId(payload.userId())
                .borrowRecordId(payload.borrowRecordId())
                .type(payload.type())
                .message(payload.message())
                .isRead(false)
                .sentAt(OffsetDateTime.now())
                .build();
        notificationRepository.save(notification);
        String userEmail = null;
        String fullName = null;
        try {
            ApiResult<UserResponse> userApiResult = userServiceClient.getUserById(payload.userId());
            if (userApiResult != null && userApiResult.getData() != null) {
                userEmail = userApiResult.getData().email();
                fullName = userApiResult.getData().fullName();
            }
        } catch (Exception e) {
            log.warn("[UserService Feign Error] Không thể lấy email của userId={}: {}", payload.userId(), e.getMessage());
        }
        if (userEmail != null && !userEmail.isBlank()) {
            emailServiceImpl.sendNotificationEmail(userEmail, fullName, payload);
        }
    }
}
