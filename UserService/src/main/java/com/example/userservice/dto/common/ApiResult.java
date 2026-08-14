package com.example.userservice.dto.common;

public record ApiResult<T>(
        int code,
        String status,
        String message,
        T data
) {
}
