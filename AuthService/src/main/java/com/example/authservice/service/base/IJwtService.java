package com.example.authservice.service.base;

import com.example.authservice.entity.Account;
import com.example.authservice.entity.RefreshToken;
import com.example.authservice.entity.Role;
import io.jsonwebtoken.Claims;

import java.time.OffsetDateTime;
import java.util.Set;

public interface IJwtService {
    String generateToken(String username, Long userId, Set<Role> roles);

    RefreshToken generateRefreshToken(Account account);

    RefreshToken verifyToken(RefreshToken refreshToken);

    RefreshToken rotateRefreshToken(RefreshToken refreshToken);

    Claims extractJwtClaims(String token);

    String extractJwtId(String token);

    OffsetDateTime extractJwtExpire(String token);
}
