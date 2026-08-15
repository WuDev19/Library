package com.example.notificationservice.exception;

import com.example.notificationservice.dto.common.ApiResult;
import com.example.notificationservice.dto.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResult<Object>> handleBusinessException(BusinessException ex) {
        ErrorResponse err = ex.getErrorResponse();
        log.error("[BusinessException] Code: {}, Message: {}", err.getCode(), err.getMessage());
        return ApiResponse.error(err.getMessage(), err.getCode(), err.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Object>> handleGenericException(Exception ex) {
        log.error("[SystemException] ", ex);
        return ApiResponse.error("Lỗi hệ thống: " + ex.getMessage(), 500, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
