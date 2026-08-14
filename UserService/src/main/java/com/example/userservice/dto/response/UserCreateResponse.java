package com.example.userservice.dto.response;

public record UserCreateResponse(
        Long userId,
        String fullName,
        String email,
        String phone
) {
}
