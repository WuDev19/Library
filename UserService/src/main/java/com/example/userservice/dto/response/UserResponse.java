package com.example.userservice.dto.response;

import java.time.OffsetDateTime;

public record UserResponse(
        Long userId,
        String fullName,
        String email,
        String phone,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
