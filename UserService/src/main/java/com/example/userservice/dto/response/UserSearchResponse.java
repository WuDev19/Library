package com.example.userservice.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Response trả về thông tin đầy đủ của user, bao gồm danh sách sách đang mượn.
 * Chỉ dành cho LIBRARIAN khi tìm kiếm user.
 */
public record UserSearchResponse(
        Long userId,
        String fullName,
        String email,
        String phone,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        int borrowingCount,
        List<BorrowedBookResponse> borrowedBooks
) {
}
