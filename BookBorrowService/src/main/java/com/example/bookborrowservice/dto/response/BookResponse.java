package com.example.bookborrowservice.dto.response;

import java.time.OffsetDateTime;

public record BookResponse(
        Long bookId,
        String code,
        String title,
        Long categoryId,
        String categoryName,
        String author,
        String publisher,
        Short publishedYear,
        String isbn,
        String description,
        Integer totalQuantity,
        Integer availableQuantity,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
