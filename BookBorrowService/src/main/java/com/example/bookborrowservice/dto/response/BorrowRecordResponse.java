package com.example.bookborrowservice.dto.response;

import com.example.bookborrowservice.entity.enums.BorrowStatus;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record BorrowRecordResponse(
        Long borrowRecordId,
        String borrowCode,
        Long bookCopyId,
        Long bookId,
        String bookTitle,
        String assetCode,
        Long borrowerId,
        Long librarianId,
        LocalDate borrowDate,
        LocalDate dueDate,
        LocalDate returnDate,
        BorrowStatus status,
        String note,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
