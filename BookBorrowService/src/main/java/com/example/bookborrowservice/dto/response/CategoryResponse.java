package com.example.bookborrowservice.dto.response;

import java.time.OffsetDateTime;

public record CategoryResponse(
        Long categoryId,
        String code,
        String name,
        String description,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
