package com.example.authservice.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserCreateRequest(
        @NotNull
        Long userId,
        @NotBlank
        String email,
        @NotBlank
        String fullName,
        @NotBlank
        String phone
) {
}
