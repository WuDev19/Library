package com.example.notificationservice.dto.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResponse {

    public static <T> ResponseEntity<ApiResult<T>> success(T data, String message, int code) {
        ApiResult<T> result = ApiResult.<T>builder()
                .code(code)
                .status("Success")
                .message(message)
                .data(data)
                .build();
        return ResponseEntity.ok(result);
    }

    public static <T> ResponseEntity<ApiResult<T>> error(String message, int code, HttpStatus status) {
        ApiResult<T> result = ApiResult.<T>builder()
                .code(code)
                .status("Error")
                .message(message)
                .data(null)
                .build();
        return ResponseEntity.status(status).body(result);
    }
}
