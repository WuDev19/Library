package com.example.bookborrowservice.service.impl;

import com.example.bookborrowservice.dto.common.PageResponse;
import com.example.bookborrowservice.dto.event.NotificationEventPayload;
import com.example.bookborrowservice.dto.request.BorrowRequest;
import com.example.bookborrowservice.dto.request.ReturnRequest;
import com.example.bookborrowservice.dto.response.BorrowRecordResponse;
import com.example.bookborrowservice.dto.response.BorrowedBookResponse;
import com.example.bookborrowservice.entity.Book;
import com.example.bookborrowservice.entity.BookCopy;
import com.example.bookborrowservice.entity.BorrowRecord;
import com.example.bookborrowservice.entity.OutboxEvent;
import com.example.bookborrowservice.entity.enums.BorrowStatus;
import com.example.bookborrowservice.entity.enums.CopyStatus;
import com.example.bookborrowservice.entity.enums.NotificationType;
import com.example.bookborrowservice.entity.enums.OutboxStatus;
import com.example.bookborrowservice.exception.BusinessException;
import com.example.bookborrowservice.exception.ErrorResponse;
import com.example.bookborrowservice.mapper.BorrowMapper;
import com.example.bookborrowservice.repository.BookCopyRepository;
import com.example.bookborrowservice.repository.BookRepository;
import com.example.bookborrowservice.repository.BorrowRecordRepository;
import com.example.bookborrowservice.repository.OutboxEventRepository;
import com.example.bookborrowservice.service.base.IBorrowService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements IBorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final BorrowMapper borrowMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void borrowBook(BorrowRequest request, Long librarianId, Long authenticatedUserId) {
        if (request.dueDate().isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorResponse.DUE_DATE_INVALID);
        }
        Long targetBorrowerId = (librarianId != null && request.borrowerId() != null)
                ? request.borrowerId()
                : authenticatedUserId;
        Book targetBook = bookRepository.findByIdWithCategoryForUpdate(request.bookId())
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        List<BookCopy> availableCopies = bookCopyRepository.findByBookBookIdAndStatusForUpdate(request.bookId(), CopyStatus.AVAILABLE);
        if (availableCopies.isEmpty()) {
            throw new BusinessException(ErrorResponse.BOOK_OUT_OF_STOCK);
        }

        BookCopy copyToBorrow = availableCopies.get(0);
        copyToBorrow.setStatus(CopyStatus.BORROWED);
        bookCopyRepository.save(copyToBorrow);

        if (targetBook.getAvailableQuantity() != null && targetBook.getAvailableQuantity() > 0) {
            targetBook.setAvailableQuantity(targetBook.getAvailableQuantity() - 1);
            bookRepository.save(targetBook);
        }

        String borrowCode = "BRW-" + System.currentTimeMillis();

        BorrowRecord record = BorrowRecord.builder()
                .borrowCode(borrowCode)
                .bookCopy(copyToBorrow)
                .borrowerId(targetBorrowerId)
                .librarianId(librarianId)
                .borrowDate(LocalDate.now())
                .dueDate(request.dueDate())
                .status(BorrowStatus.BORROWING)
                .note(request.note())
                .build();

        borrowRecordRepository.save(record);
    }

    @Override
    @Transactional
    public void returnBook(ReturnRequest request, Long librarianId) {
        BorrowRecord record = borrowRecordRepository.findByBorrowCodeWithDetails(request.borrowCode())
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));

        if (record.getStatus() != BorrowStatus.BORROWING && record.getStatus() != BorrowStatus.OVERDUE) {
            throw new BusinessException(ErrorResponse.BORROW_RECORD_NOT_ACTIVE);
        }

        BookCopy copy = record.getBookCopy();
        copy.setStatus(CopyStatus.AVAILABLE);
        bookCopyRepository.save(copy);

        if (copy.getBook() != null) {
            Book book = copy.getBook();
            if (book.getAvailableQuantity() != null) {
                book.setAvailableQuantity(book.getAvailableQuantity() + 1);
                bookRepository.save(book);
            }
        }

        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowStatus.RETURNED);
        record.setLibrarianId(librarianId);
        if (request.note() != null && !request.note().trim().isEmpty()) {
            record.setNote(record.getNote() != null
                    ? record.getNote() + " | Trả sách: " + request.note()
                    : "Trả sách: " + request.note());
        }

        borrowRecordRepository.save(record);
    }

    @Override
    public PageResponse<BorrowRecordResponse> getBorrowRecordsByUser(Long userId, Pageable pageable) {
        Page<BorrowRecord> page = borrowRecordRepository.findByBorrowerUserIdWithDetails(userId, pageable);
        return new PageResponse<>(
                page.getNumber(),
                page.getNumberOfElements(),
                page.getContent().stream()
                        .map(borrowMapper::mapToResponse)
                        .toList()
        );
    }

    @Override
    public PageResponse<BorrowRecordResponse> getAllBorrowRecords(Pageable pageable) {
        Page<BorrowRecord> page = borrowRecordRepository.findAllWithDetails(pageable);
        return new PageResponse<>(
                page.getNumber(),
                page.getNumberOfElements(),
                page.getContent().stream()
                        .map(borrowMapper::mapToResponse)
                        .toList()
        );
    }

    @Override
    public BorrowRecordResponse getBorrowRecordById(Long id) {
        BorrowRecord record = borrowRecordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        return borrowMapper.mapToResponse(record);
    }

    @Override
    public Map<Long, List<BorrowedBookResponse>> getActiveBorrowsByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<BorrowRecord> activeRecords = borrowRecordRepository.findActiveBorrowsByUserIdsWithDetails(
                userIds,
                List.of(BorrowStatus.BORROWING, BorrowStatus.OVERDUE)
        );

        return activeRecords.stream()
                .collect(Collectors.groupingBy(
                        BorrowRecord::getBorrowerId,
                        Collectors.mapping(borrowMapper::mapToBorrowedBookResponse, Collectors.toList())
                ));
    }

    @Override
    @Transactional
    public Map<String, Object> scanOverdueBorrows() {
        LocalDate today = LocalDate.now();
        List<BorrowRecord> overdueCandidates = borrowRecordRepository.findOverdueCandidateRecordsWithDetails(
                BorrowStatus.BORROWING,
                today
        );

        int scannedCount = 0;
        for (BorrowRecord record : overdueCandidates) {
            record.setStatus(BorrowStatus.OVERDUE);
            long daysOverdue = ChronoUnit.DAYS.between(record.getDueDate(), today);
            long fine = Math.max(0, daysOverdue * 5000L);
            record.setNote((record.getNote() != null ? record.getNote() + " | " : "") +
                    "Quá hạn " + daysOverdue + " ngày. Phạt: " + fine + " VNĐ");
            borrowRecordRepository.save(record);
            scannedCount++;
            saveOutboxNotificationEvent(record, NotificationType.OVERDUE, daysOverdue, fine);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("scannedCount", scannedCount);
        return result;
    }

    private void saveOutboxNotificationEvent(BorrowRecord record, NotificationType type, long daysOverdue, long fine) {
        String aggregateId = String.valueOf(record.getBorrowRecordId());

        if (outboxEventRepository.existsByAggregateIdAndEventType(aggregateId, type.name())) {
            return;
        }
        String bookTitle = (record.getBookCopy() != null && record.getBookCopy().getBook() != null)
                ? record.getBookCopy().getBook().getTitle()
                : "N/A";
        String message = String.format("Sách '%s' (Mã phiếu: %s) đã quá hạn %d ngày (đã quá hạn vào ngày %s). Số tiền phạt: %d VNĐ. Vui lòng trả sách ngay!",
                bookTitle, record.getBorrowCode(), daysOverdue, record.getDueDate(), fine);
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
        try {
            String jsonPayload = objectMapper.writeValueAsString(payloadDto);
            OutboxEvent event = OutboxEvent.builder()
                    .aggregateType("BORROW_RECORD")
                    .aggregateId(aggregateId)
                    .eventType(type.name())
                    .payload(jsonPayload)
                    .status(OutboxStatus.PENDING)
                    .build();
            outboxEventRepository.save(event);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
