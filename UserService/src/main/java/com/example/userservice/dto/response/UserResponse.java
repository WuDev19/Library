package com.example.userservice.dto.response;

import java.time.OffsetDateTime;

/**
 * Response thông tin user cơ bản (dành cho get/update).
 */
public record UserResponse(
        Long userId,
        String fullName,
        String email,
        String phone,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
