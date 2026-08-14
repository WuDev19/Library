package com.example.authservice.dto.response;

public record UserCreateResponse(
        Long userId,
        String fullName,
        String email,
        String phone
) {
}
