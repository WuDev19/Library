package com.example.apigateway.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@SuppressWarnings("NullableProblems")
@Component
public class MyJwtDecoder implements ReactiveJwtDecoder {
    private final NimbusReactiveJwtDecoder nimbusJwtDecoder;
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public MyJwtDecoder(
            @Value("${JWT_SECRET_KEY}") String JWT_SECRET_KEY,
            ReactiveRedisTemplate<String, Object> redisTemplate
    ) {
        this.nimbusJwtDecoder = NimbusReactiveJwtDecoder
                .withSecretKey(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Mono<Jwt> decode(String token) throws JwtException {
        return nimbusJwtDecoder.decode(token)
                .flatMap(jwt -> {
                    String jti = jwt.getId();
                    if (jti == null || jti.isBlank()) {
                        return Mono.error(
                                new JwtException("JWT thiếu jti")
                        );
                    }
                    String key = "blacklist_token:jwt_id:" + jti;
                    return redisTemplate.hasKey(key)
                            .flatMap(exists -> exists
                                    ? Mono.error(
                                    new JwtException("Token đang trong danh sách đen"))
                                    : Mono.just(jwt)
                            );
                });
    }

}
