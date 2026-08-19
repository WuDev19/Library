package com.example.notificationservice.listener;

import com.example.grpc.user.v1.GetUserRequest;
import com.example.grpc.user.v1.UserResponse;
import com.example.grpc.user.v1.UserServiceGrpc;
import com.example.notificationservice.client.UserService;
import com.example.notificationservice.dto.common.ApiResult;
import com.example.notificationservice.dto.event.NotificationEventPayload;
import com.example.notificationservice.entity.Notification;
import com.example.notificationservice.repository.NotificationRepository;
import com.example.notificationservice.service.base.IEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationKafkaListener {

    private final NotificationRepository notificationRepository;
    private final IEmailService emailService;
    private final ObjectMapper objectMapper;
    private final UserServiceGrpc.UserServiceBlockingStub stub;

    @KafkaListener(topics = "notification-events", groupId = "notification-group")
    @Transactional
    public void handleNotificationEvent(String messagePayload, Acknowledgment ack) {
        try {
            NotificationEventPayload payload;
            try {
                payload = objectMapper.readValue(messagePayload, NotificationEventPayload.class);
            } catch (Exception e) {
                log.error("[Kafka Consumer Error] Lỗi parse JSON message payload: {}", e.getMessage(), e);
                return;
            }

            boolean exists = notificationRepository.existsByBorrowRecordIdAndType(payload.borrowRecordId(), payload.type());
            if (exists) {
                log.warn("[Kafka Consumer Skip] Thông báo đã tồn tại cho borrowRecordId={} và type={}", payload.borrowRecordId(), payload.type());
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
                UserResponse userApiResult = stub.getUser(GetUserRequest.newBuilder()
                        .setUserId(payload.userId())
                        .build()
                );
                if (userApiResult != null) {
                    userEmail = userApiResult.getEmail();
                    fullName = userApiResult.getFullName();
                    log.debug("Email: " + userEmail + "\n" + "FullName: " + fullName);
                } else {
                    log.error("Lỗi gọi GRPC");
                }
            } catch (Exception e) {
                log.warn("[UserService Feign Error] Không thể lấy thông tin user cho userId={}: {}", payload.userId(), e.getMessage());
            }

            if (userEmail != null && !userEmail.isBlank()) {
                try {
                    emailService.sendNotificationEmail(userEmail, fullName, payload);
                } catch (Exception e) {
                    log.error("[Email Error] Không thể gửi email cho userId={}: {}", payload.userId(), e.getMessage());
                }
            }
        } finally {
            if (ack != null) {
                ack.acknowledge();
            }
        }
    }
}
