package com.example.bookborrowservice.exception;

import com.example.bookborrowservice.dto.common.ApiResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        e.printStackTrace();
        return ApiResponse.error(e.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException e) {
        e.printStackTrace();
        var errorResponse = e.getErrorResponse();
        return ApiResponse.error(
                errorResponse.getMessage(),
                errorResponse.getCode(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        return ApiResponse.error(
                "Phiên đăng nhập không hợp lệ, vui lòng đăng nhập lại",
                HttpStatus.UNAUTHORIZED.value(),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        return ApiResponse.error(
                ErrorResponse.ACCESS_DENIED.getMessage(),
                ErrorResponse.ACCESS_DENIED.getCode(),
                HttpStatus.FORBIDDEN
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return ApiResponse.error(
                ErrorResponse.PARSE_JSON.getMessage(),
                ErrorResponse.PARSE_JSON.getCode(),
                HttpStatus.BAD_REQUEST
        );
    }

    @Override
    protected @Nullable ResponseEntity<Object> handleMethodValidationException(
            MethodValidationException e,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        return ApiResponse.error(
                ErrorResponse.FIELD_INVALID.getMessage(),
                ErrorResponse.FIELD_INVALID.getCode(),
                HttpStatus.BAD_REQUEST
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<String> detailMessage = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    return fieldName + ": " + errorMessage;
                })
                .collect(Collectors.toList());

        return ApiResponse.error(
                detailMessage,
                ErrorResponse.OBJECT_INVALID.getCode(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        e.printStackTrace();
        return ApiResponse.error(
                ErrorResponse.DATA_INTEGRITY.getMessage(),
                ErrorResponse.DATA_INTEGRITY.getCode(),
                HttpStatus.CONFLICT
        );
    }
}
