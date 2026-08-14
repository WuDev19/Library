package com.example.authservice.dto.common;

public record ApiResult<T>(
        int code,
        String status,
        String message,
        T data
) {
}
