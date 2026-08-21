package com.example.authservice.service.impl;

import com.example.authservice.dto.common.CRUDResponseHelper;
import com.example.authservice.dto.request.auth.LoginByUsernameRequest;
import com.example.authservice.dto.request.auth.LogoutRequest;
import com.example.authservice.dto.request.auth.SignUpWithUsernameRequest;
import com.example.authservice.dto.response.LoginResponse;
import com.example.authservice.entity.Account;
import com.example.authservice.entity.RefreshToken;
import com.example.authservice.entity.Role;
import com.example.authservice.exception.BusinessException;
import com.example.authservice.exception.ErrorResponse;
import com.example.authservice.repository.AccountRepository;
import com.example.authservice.repository.RefreshTokenRepository;
import com.example.authservice.repository.RoleRepository;
import com.example.authservice.service.base.IAuthenticationService;
import com.example.authservice.service.base.IJwtService;
import com.example.authservice.service.base.IRedisService;
import com.example.authservice.utils.TimeUtils;
import com.example.grpc.user.v1.UserCreateRequest;
import com.example.grpc.user.v1.UserServiceGrpc;
import com.google.protobuf.Empty;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final IJwtService jwtService;
    private final IRedisService redisService;
    private final UserServiceGrpc.UserServiceBlockingStub stub;
    private static final String BLACK_KEY = "blacklist_token:jwt_id:";

    @Transactional
    @Override
    public LoginResponse login(LoginByUsernameRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();
        Account account = accountRepository.findByUsername(username).orElseThrow(() ->
                new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        boolean isMatch = bCryptPasswordEncoder.matches(password, account.getPassword());
        if (!isMatch) {
            throw new BusinessException(ErrorResponse.PASSWORD_NOT_TRUE);
        }
        account.setActive(true);
        String token = jwtService.generateToken(username, account.getUserId(), account.getRoles());
        RefreshToken refreshToken = jwtService.generateRefreshToken(account);
        refreshTokenRepository.save(refreshToken);
        accountRepository.save(account);
        return new LoginResponse(token, refreshToken.getRefreshToken());
    }

    @Transactional
    @Override
    public Map<String, Object> signUpWithUsername(SignUpWithUsernameRequest request) {
        String password = request.password();
        String passwordConfirm = request.passwordConfirm();
        if (!password.equals(passwordConfirm)) {
            throw new BusinessException(ErrorResponse.PASSWORD_NOT_MATCH);
        }
        Role role = roleRepository.findByRoleName(request.role()).orElseThrow(() ->
                new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        Account account = Account.builder()
                .username(request.username())
                .email(request.email())
                .password(bCryptPasswordEncoder.encode(password))
                .roles(Set.of(role))
                .build();
        accountRepository.save(account);
        try {
            Empty response = stub.createUser(UserCreateRequest.newBuilder()
                    .setEmail(request.email())
                    .setFullName(request.fullName())
                    .setPhone(request.phone())
                    .setUserId(account.getUserId())
                    .build());
        } catch (StatusRuntimeException e) {
            throw new BusinessException(ErrorResponse.USER_CREATE_ERROR);
        }
        return CRUDResponseHelper.createSuccess();
    }

    @Transactional
    @Override
    public LoginResponse refreshToken(String refToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refToken)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        RefreshToken validRefreshToken = jwtService.verifyToken(refreshToken);
        RefreshToken newRefreshToken = jwtService.rotateRefreshToken(validRefreshToken);
        RefreshToken saved = refreshTokenRepository.save(newRefreshToken);
        Account account = saved.getAccount();
        String token = jwtService.generateToken(account.getUsername(), account.getUserId(), account.getRoles());
        return new LoginResponse(
                token,
                saved.getRefreshToken()
        );
    }

    @Transactional
    @Override
    public Map<String, Object> logout(LogoutRequest logoutRequest) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(logoutRequest.refreshToken()).orElseThrow(() ->
                new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        refreshToken.setIsRevoked(true);
        String jwtId = jwtService.extractJwtId(logoutRequest.accessToken());
        OffsetDateTime expireDate = jwtService.extractJwtExpire(logoutRequest.accessToken());
        Duration ttl = Duration.between(TimeUtils.now(), expireDate);
        if (!ttl.isNegative() && !ttl.isZero()) {
            redisService.set(
                    BLACK_KEY + jwtId,
                    true,
                    ttl
            );
        }
        refreshTokenRepository.save(refreshToken);
        return CRUDResponseHelper.modifySuccess();
    }
}
