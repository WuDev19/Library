package com.example.bookborrowservice.dto.response;

import com.example.bookborrowservice.entity.enums.CopyStatus;

public record BookCopyResponse(
        Long bookCopyId,
        Long bookId,
        String assetCode,
        CopyStatus status,
        String note
) {
}
