package com.example.userservice.config;

import com.example.userservice.exception.BusinessException;
import com.example.userservice.exception.ErrorResponse;
import io.grpc.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.grpc.server.GlobalServerInterceptor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

@Component
@GlobalServerInterceptor
public class GrpcServerInterceptor implements ServerInterceptor {

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    public static final Context.Key<Claims> CLAIMS_CONTEXT_KEY = Context.key("claims");

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {
        try {
            String token = Objects.requireNonNull(headers.get(Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER)))
                    .substring(7);
            Claims claims = validateToken(token);
            Context context = Context.current().withValue(CLAIMS_CONTEXT_KEY, claims);
            return Contexts.interceptCall(context, call, headers, next);
        } catch (BusinessException e) {
            ErrorResponse error = e.getErrorResponse();
            Status status = switch (e.getErrorResponse().getCode()) {
                case 1003 -> Status.UNAUTHENTICATED.withDescription(error.getMessage());
                case 1022 -> Status.UNKNOWN.withDescription(error.getMessage());
                default -> Status.INTERNAL.withDescription(error.getMessage());
            };
            call.close(status, headers);
            return new ServerCall.Listener<>() {
            };
        } catch (Exception e) {
            call.close(Status.INTERNAL.withDescription("Lỗi hệ thống"), headers);
            return new ServerCall.Listener<>() {
            };
        }
    }

    private Claims validateToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Date expire = claims.getExpiration();
        if (expire.before(new Date())) {
            throw new BusinessException(ErrorResponse.JWT_EXCEPTION);
        }
        String subject = claims.getSubject();
        if (!subject.startsWith("GRPC")) {
            throw new BusinessException(ErrorResponse.INTERNAL_GRPC_EXCEPTION);
        }
        return claims;
    }
}
