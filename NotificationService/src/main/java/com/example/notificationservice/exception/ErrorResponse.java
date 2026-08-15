package com.example.notificationservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorResponse {
    RESOURCE_NOT_FOUND("Tài nguyên không tồn tại", 404, HttpStatus.NOT_FOUND),
    UNAUTHORIZED("Bạn không có quyền truy cập", 403, HttpStatus.FORBIDDEN),
    SYSTEM_ERROR("Lỗi hệ thống", 500, HttpStatus.INTERNAL_SERVER_ERROR);

    private final String message;
    private final int code;
    private final HttpStatus status;
}
