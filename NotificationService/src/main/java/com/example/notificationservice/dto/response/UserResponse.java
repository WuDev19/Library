package com.example.notificationservice.dto.response;

public record UserResponse(
        Long userId,
        String username,
        String email,
        String fullName,
        String phone,
        String roleName
) {
}
