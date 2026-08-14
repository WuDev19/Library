package com.example.library.service.base;

import com.example.library.dto.request.auth.LoginByUsernameRequest;
import com.example.library.dto.request.auth.LogoutRequest;
import com.example.library.dto.request.auth.SignUpWithUsernameRequest;
import com.example.library.dto.response.LoginResponse;

import java.util.Map;

public interface IAuthenticationService {
    boolean checkAccessTokenInBlacklist(String tokenId);

    boolean checkUserExist(Long userId);

    LoginResponse refreshToken(String refToken);

    Map<String, Object> logout(LogoutRequest logoutRequest);

    LoginResponse login(LoginByUsernameRequest loginRequest);

    Map<String, Object> signUpWithUsername(SignUpWithUsernameRequest request);

}
