package com.example.apigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@Order(-2)
@RequiredArgsConstructor
public class GlobalExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public @NonNull Mono<Void> handle(ServerWebExchange exchange, @NonNull Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        HttpStatus status;
        int code;
        String message;

        if (ex instanceof JwtException || ex instanceof AuthenticationException) {
            status = HttpStatus.UNAUTHORIZED;
            code = 401;
            message = "Phiên đăng nhập không hợp lệ hoặc token đã bị thu hồi: " + ex.getMessage();
        } else if (ex instanceof AccessDeniedException) {
            status = HttpStatus.FORBIDDEN;
            code = 403;
            message = "Bạn không có quyền truy cập tài nguyên này";
        } else if (ex instanceof ResponseStatusException rse) {
            status = HttpStatus.valueOf(rse.getStatusCode().value());
            code = status.value();
            message = rse.getReason() != null ? rse.getReason() : rse.getMessage();
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            code = 500;
            message = "Lỗi cổng kết nối ApiGateway: " + ex.getMessage();
        }

        log.error("[ApiGateway Exception] Path: {}, Status: {}, Message: {}",
                exchange.getRequest().getPath(), status, message, ex);

        response.setStatusCode(status);

        Map<String, Object> body = new HashMap<>();
        body.put("code", code);
        body.put("status", "Error");
        body.put("message", message);
        body.put("data", null);

        byte[] bytes;
        try {
            bytes = objectMapper.writeValueAsBytes(body);
        } catch (JsonProcessingException e) {
            bytes = ("{\"code\":500,\"status\":\"Error\",\"message\":\"Lỗi hệ thống Gateway\",\"data\":null}").getBytes();
        }

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
