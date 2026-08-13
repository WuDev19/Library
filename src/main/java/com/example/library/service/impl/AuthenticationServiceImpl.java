package com.example.library.service.impl;

import com.example.library.dto.common.CRUDResponseHelper;
import com.example.library.dto.request.auth.LoginByUsernameRequest;
import com.example.library.dto.request.auth.LogoutRequest;
import com.example.library.dto.request.auth.SignUpWithUsernameRequest;
import com.example.library.dto.response.LoginResponse;
import com.example.library.entity.BlackListAccessToken;
import com.example.library.entity.RefreshToken;
import com.example.library.entity.Role;
import com.example.library.entity.User;
import com.example.library.exception.BusinessException;
import com.example.library.exception.ErrorResponse;
import com.example.library.repository.BlackListAccessTokenRepository;
import com.example.library.repository.RefreshTokenRepository;
import com.example.library.repository.RoleRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.base.IAuthenticationService;
import com.example.library.service.base.IJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements IAuthenticationService {

    private final BlackListAccessTokenRepository blackListAccessTokenRepository;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final IJwtService iJwtService;
    private final RoleRepository roleRepository;

    @Override
    public boolean checkAccessTokenInBlacklist(String tokenId) {
        return blackListAccessTokenRepository.existsByTokenId(tokenId);
    }

    @Override
    public boolean checkUserExist(Long userId) {
        return userRepository.existsById(userId);
    }

    @Transactional
    @Override
    public LoginResponse login(LoginByUsernameRequest loginRequest) {
        String username = loginRequest.username();
        String password = loginRequest.password();
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        boolean isMatch = bCryptPasswordEncoder.matches(password, user.getPasswordHash());
        if (!isMatch) {
            throw new BusinessException(ErrorResponse.PASSWORD_NOT_TRUE);
        }
        user.setIsActive(true);
        String token = iJwtService.generateToken(username, user.getUserId(), user.getRoles());
        RefreshToken refreshToken = iJwtService.generateRefreshToken(user);
        refreshTokenRepository.save(refreshToken);
        userRepository.save(user);
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
        Role role = roleRepository.findByRoleName(request.role()).orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .passwordHash(bCryptPasswordEncoder.encode(password))
                .fullName(request.fullName())
                .phone(request.phone())
                .roles(Set.of(role))
                .build();
        userRepository.save(user);
        return CRUDResponseHelper.createSuccess();
    }

    @Transactional
    @Override
    public LoginResponse refreshToken(String refToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(refToken)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        RefreshToken validRefreshToken = iJwtService.verifyToken(refreshToken);
        RefreshToken newRefreshToken = iJwtService.rotateRefreshToken(validRefreshToken);
        RefreshToken saved = refreshTokenRepository.save(newRefreshToken);
        User user = saved.getUserRefreshToken();
        String token = iJwtService.generateToken(user.getUsername(), user.getUserId(), user.getRoles());
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
        String jwtId = iJwtService.extractJwtId(logoutRequest.accessToken());
        OffsetDateTime expire = iJwtService.extractJwtExpire(logoutRequest.accessToken());
        BlackListAccessToken blacklistAccessToken = BlackListAccessToken.builder()
                .tokenId(jwtId)
                .expireDate(expire)
                .build();
        refreshTokenRepository.save(refreshToken);
        blackListAccessTokenRepository.save(blacklistAccessToken);
        return CRUDResponseHelper.modifySuccess();
    }
}
