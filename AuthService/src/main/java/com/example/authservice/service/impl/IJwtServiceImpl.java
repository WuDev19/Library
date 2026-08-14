package com.example.authservice.service.impl;

import com.example.authservice.constants.StringCommon;
import com.example.authservice.entity.Account;
import com.example.authservice.entity.RefreshToken;
import com.example.authservice.entity.Role;
import com.example.authservice.exception.BusinessException;
import com.example.authservice.exception.ErrorResponse;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.service.base.IJwtService;
import com.example.authservice.utils.TimeUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IJwtServiceImpl implements IJwtService {

    @Value("${JWT_SECRET_KEY}")
    private String JWT_SECRET_KEY;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public String generateToken(String username, Long userId, Set<Role> roles) {
        var expiration = 3 * 24 * 60 * 60 * 1000;
        String roleName = (roles != null && !roles.isEmpty())
                ? roles.iterator().next().getRoleName()
                : "BORROWER";
        return Jwts.builder()
                .subject(username)
                .claim(StringCommon.ROLES, roleName)
                .claim(StringCommon.USER_ID, userId)
                .id(UUID.randomUUID().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS256)
                .compact();
    }

    @Override
    public RefreshToken generateRefreshToken(Account account) {
        String token = UUID.randomUUID().toString();
        return RefreshToken.builder()
                .account(account)
                .refreshToken(token)
                .expireDate(TimeUtils.now().plusDays(7))
                .build();
    }

    @Override
    public RefreshToken verifyToken(RefreshToken refreshToken) {
        if (refreshToken.getIsRevoked()) {
            refreshTokenRepository.deleteByAccount(refreshToken.getAccount());
            throw new BusinessException(ErrorResponse.REFRESH_TOKEN_REVOKED);
        }
        if (refreshToken.getExpireDate().isBefore(TimeUtils.now())) {
            throw new BusinessException(ErrorResponse.JWT_EXCEPTION);
        }
        return refreshToken;
    }

    @Override
    public RefreshToken rotateRefreshToken(RefreshToken refreshToken) {
        refreshToken.setIsRevoked(true);
        refreshTokenRepository.save(refreshToken);
        return generateRefreshToken(refreshToken.getAccount());
    }

    @Override
    public Claims extractJwtClaims(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public String extractJwtId(String token) {
        return extractJwtClaims(token).getId();
    }

    @Override
    public OffsetDateTime extractJwtExpire(String token) {
        return extractJwtClaims(token)
                .getExpiration()
                .toInstant()
                .atZone(ZoneId.of(StringCommon.TIME_ZONE_VN))
                .toOffsetDateTime();
    }
}
