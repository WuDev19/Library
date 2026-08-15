package com.example.bookborrowservice.dto.response;

import java.time.OffsetDateTime;

public record BorrowedBookResponse(
        Long borrowId,
        Long bookId,
        String bookTitle,
        OffsetDateTime borrowDate,
        OffsetDateTime dueDate
) {
}
