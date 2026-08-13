package com.example.library.service.impl;

import com.example.library.entity.BorrowRecord;
import com.example.library.entity.enums.BorrowStatus;
import com.example.library.entity.enums.NotificationType;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.NotificationRepository;
import com.example.library.service.base.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LibraryNotificationJob {

    private final BorrowRecordRepository borrowRecordRepository;
    private final NotificationRepository notificationRepository;
    private final INotificationService notificationService;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void runNotificationScanScheduled() {
        log.info("Starting scheduled library notification scan...");
        runScan();
        log.info("Finished scheduled library notification scan.");
    }

    @Transactional
    public void runScan() {
        LocalDate today = LocalDate.now();
        List<BorrowRecord> activeRecords = borrowRecordRepository.findByStatusIn(
                List.of(BorrowStatus.BORROWING, BorrowStatus.OVERDUE)
        );

        for (BorrowRecord record : activeRecords) {
            String bookTitle = record.getBookCopy().getBook().getTitle();
            String borrowCode = record.getBorrowCode();

            if (record.getDueDate().isBefore(today)) {
                if (record.getStatus() == BorrowStatus.BORROWING) {
                    record.setStatus(BorrowStatus.OVERDUE);
                    borrowRecordRepository.save(record);
                }

                if (!notificationRepository.existsByBorrowRecordBorrowRecordIdAndType(record.getBorrowRecordId(), NotificationType.OVERDUE)) {
                    String message = String.format("Sách '%s' (Mã mượn: %s) đã quá hạn trả vào ngày %s. Vui lòng trả sách ngay!", 
                            bookTitle, borrowCode, record.getDueDate());
                    notificationService.createNotification(record, NotificationType.OVERDUE, message);
                    log.info("Sent OVERDUE notification for borrow code: {}", borrowCode);
                }
            }
            else if (record.getDueDate().equals(today) || record.getDueDate().equals(today.plusDays(1))) {
                if (!notificationRepository.existsByBorrowRecordBorrowRecordIdAndType(record.getBorrowRecordId(), NotificationType.DUE_SOON)) {
                    String message = String.format("Sách '%s' (Mã mượn: %s) chuẩn bị đến hạn trả vào ngày %s. Vui lòng trả đúng hạn!", 
                            bookTitle, borrowCode, record.getDueDate());
                    notificationService.createNotification(record, NotificationType.DUE_SOON, message);
                    log.info("Sent DUE_SOON notification for borrow code: {}", borrowCode);
                }
            }

            if (!notificationRepository.existsByBorrowRecordBorrowRecordIdAndType(record.getBorrowRecordId(), NotificationType.STILL_BORROWING)) {
                String message = String.format("Bạn đang mượn cuốn sách '%s' (Mã mượn: %s) từ ngày %s. Hạn trả là ngày %s.", 
                        bookTitle, borrowCode, record.getBorrowDate(), record.getDueDate());
                notificationService.createNotification(record, NotificationType.STILL_BORROWING, message);
                log.info("Sent STILL_BORROWING notification for borrow code: {}", borrowCode);
            }
        }
    }
}
