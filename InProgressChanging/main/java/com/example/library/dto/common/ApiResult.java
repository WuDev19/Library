package com.example.library.dto.common;

public record ApiResult<T>(
        int code,
        String status,
        String message,
        T data
) {
}
