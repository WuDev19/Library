package com.example.userservice.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("NullableProblems")
@Component
public class MyJwtDecoder implements JwtDecoder {

    private final NimbusJwtDecoder jwtDecoder;
    private final StringRedisTemplate redisTemplate;

    public MyJwtDecoder(
            @Value("${jwt.secret-key}") String JWT_SECRET_KEY,
            StringRedisTemplate redisTemplate
    ) {
        this.jwtDecoder = NimbusJwtDecoder
                .withSecretKey(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Jwt decode(String token) throws JwtException {
        Jwt jwt = jwtDecoder.decode(token);
        String jti = jwt.getId();
        if (jti == null || jti.isBlank()) {
            throw new JwtException("JWT thiếu jti");
        }
        String key = "blacklist_token:jwt_id:" + jti;
        Boolean isBlacklisted = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            throw new JwtException("Token đang trong danh sách đen");
        }
        return jwt;
    }
}
