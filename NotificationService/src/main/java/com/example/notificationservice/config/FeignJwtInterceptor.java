package com.example.notificationservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Component
@Slf4j
public class FeignJwtInterceptor implements RequestInterceptor {

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;

    @Override
    public void apply(RequestTemplate template) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() instanceof Jwt jwt) {
            template.header("Authorization", "Bearer " + jwt.getTokenValue());
            return;
        }

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            String authHeader = attributes.getRequest().getHeader("Authorization");
            if (authHeader != null && !authHeader.isBlank()) {
                template.header("Authorization", authHeader);
                return;
            }
        }

        // chỗ này cho trường hợp chạy ở luồng khác như là từ kafka listener
        try {
            String systemToken = Jwts.builder()
                    .subject("system_notification_service")
                    .claim("roles", "SYSTEM")
                    .claim("userId", 0L)
                    .id(UUID.randomUUID().toString())
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + 60000))
                    .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                    .compact();
            template.header("Authorization", "Bearer " + systemToken);
        } catch (Exception e) {
            log.error("Lỗi khi tạo System Token cho Feign request: {}", e.getMessage());
        }
    }
}
