package com.example.bookborrowservice.job;

import com.example.bookborrowservice.dto.event.NotificationEventPayload;
import com.example.bookborrowservice.entity.BorrowRecord;
import com.example.bookborrowservice.entity.OutboxEvent;
import com.example.bookborrowservice.entity.enums.BorrowStatus;
import com.example.bookborrowservice.entity.enums.NotificationType;
import com.example.bookborrowservice.entity.enums.OutboxStatus;
import com.example.bookborrowservice.repository.BorrowRecordRepository;
import com.example.bookborrowservice.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LibraryNotificationJob {

    private final BorrowRecordRepository borrowRecordRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void runNotificationScanScheduled() {
        runScan();
    }

    public void runScan() {
        LocalDate today = LocalDate.now();
        List<BorrowRecord> activeRecords = borrowRecordRepository.findActiveBorrowsByStatusesWithDetails(
                List.of(BorrowStatus.BORROWING, BorrowStatus.OVERDUE)
        );

        for (BorrowRecord record : activeRecords) {
            String bookTitle = (record.getBookCopy() != null && record.getBookCopy().getBook() != null)
                    ? record.getBookCopy().getBook().getTitle()
                    : "N/A";
            String borrowCode = record.getBorrowCode();
            String aggregateId = String.valueOf(record.getBorrowRecordId());

            if (record.getDueDate().isBefore(today)) {
                if (record.getStatus() == BorrowStatus.BORROWING) {
                    record.setStatus(BorrowStatus.OVERDUE);
                    borrowRecordRepository.save(record);
                }

                if (!outboxEventRepository.existsByAggregateIdAndEventType(aggregateId, NotificationType.OVERDUE.name())) {
                    String message = String.format("Sách '%s' (Mã mượn: %s) đã quá hạn trả vào ngày %s. Vui lòng trả sách ngay!",
                            bookTitle, borrowCode, record.getDueDate());

                    saveOutboxEvent(record, NotificationType.OVERDUE, message, bookTitle);
                }
            }
            else if (record.getDueDate().equals(today) || record.getDueDate().equals(today.plusDays(1))) {
                if (!outboxEventRepository.existsByAggregateIdAndEventType(aggregateId, NotificationType.DUE_SOON.name())) {
                    String message = String.format("Sách '%s' (Mã mượn: %s) chuẩn bị đến hạn trả vào ngày %s. Vui lòng trả đúng hạn!",
                            bookTitle, borrowCode, record.getDueDate());
                    saveOutboxEvent(record, NotificationType.DUE_SOON, message, bookTitle);
                }
            }
            if (!outboxEventRepository.existsByAggregateIdAndEventType(aggregateId, NotificationType.STILL_BORROWING.name())) {
                String message = String.format("Bạn đang mượn cuốn sách '%s' (Mã mượn: %s) từ ngày %s. Hạn trả là ngày %s.",
                        bookTitle, borrowCode, record.getBorrowDate(), record.getDueDate());
                saveOutboxEvent(record, NotificationType.STILL_BORROWING, message, bookTitle);
            }
        }
    }

    private void saveOutboxEvent(BorrowRecord record, NotificationType type, String message, String bookTitle) {
        NotificationEventPayload payloadDto = new NotificationEventPayload(
                record.getBorrowerId(),
                record.getBorrowRecordId(),
                record.getBorrowCode(),
                bookTitle,
                type,
                message,
                record.getDueDate(),
                OffsetDateTime.now()
        );
        String jsonPayload;
        try {
            jsonPayload = objectMapper.writeValueAsString(payloadDto);
        } catch (JsonProcessingException e) {
            return;
        }
        OutboxEvent event = OutboxEvent.builder()
                .aggregateType("BORROW_RECORD")
                .aggregateId(String.valueOf(record.getBorrowRecordId()))
                .eventType(type.name())
                .payload(jsonPayload)
                .status(OutboxStatus.PENDING)
                .build();
        outboxEventRepository.save(event);
    }
}
