package com.example.bookborrowservice.dto.response;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record BookImportResponse(
        Long bookImportId,
        String importCode,
        Long bookId,
        String bookTitle,
        Integer quantity,
        Long importedBy,
        LocalDate importDate,
        String note,
        OffsetDateTime createdAt
) {
}
