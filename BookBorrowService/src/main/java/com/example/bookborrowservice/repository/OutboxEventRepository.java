package com.example.bookborrowservice.repository;

import com.example.bookborrowservice.entity.OutboxEvent;
import com.example.bookborrowservice.entity.enums.OutboxStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByStatusOrderByCreatedAtAsc(OutboxStatus status, Pageable pageable);

    List<OutboxEvent> findByStatusInAndRetryCountLessThanOrderByCreatedAtAsc(
            List<OutboxStatus> statuses,
            int maxRetryCount,
            Pageable pageable
    );

    boolean existsByAggregateIdAndEventType(String aggregateId, String eventType);
}
