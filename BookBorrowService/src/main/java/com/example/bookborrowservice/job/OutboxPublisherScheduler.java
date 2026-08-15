package com.example.bookborrowservice.job;

import com.example.bookborrowservice.entity.OutboxEvent;
import com.example.bookborrowservice.entity.enums.OutboxStatus;
import com.example.bookborrowservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxPublisherScheduler {

    private static final String KAFKA_TOPIC = "notification-events";
    private static final int MAX_RETRY_COUNT = 5;

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingOutboxEvents() {
        List<OutboxEvent> eventsToPublish = outboxEventRepository.findByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(
                List.of(OutboxStatus.PENDING, OutboxStatus.FAILED),
                MAX_RETRY_COUNT,
                PageRequest.of(0, 50)
        );
        if (eventsToPublish.isEmpty()) {
            return;
        }
        for (OutboxEvent event : eventsToPublish) {
            try {
                kafkaTemplate.send(KAFKA_TOPIC, event.getAggregateId(), event.getPayload())
                        .whenComplete((result, ex) -> {
                            if (ex == null) {
                                event.setStatus(OutboxStatus.PROCESSED);
                                event.setProcessedAt(OffsetDateTime.now());
                                outboxEventRepository.save(event);
                            } else {
                                handleFailure(event);
                            }
                        });
            } catch (Exception e) {
                handleFailure(event);
            }
        }
    }

    private void handleFailure(OutboxEvent event) {
        int currentRetry = (event.getRetryCount() != null ? event.getRetryCount() : 0) + 1;
        event.setRetryCount(currentRetry);
        if (currentRetry >= MAX_RETRY_COUNT) {
            event.setStatus(OutboxStatus.FAILED);
        } else {
            event.setStatus(OutboxStatus.PENDING);
        }
        outboxEventRepository.save(event);
    }

}
