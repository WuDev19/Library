package com.example.authservice.service.base;

import com.example.authservice.dto.request.auth.LoginByUsernameRequest;
import com.example.authservice.dto.request.auth.LogoutRequest;
import com.example.authservice.dto.request.auth.SignUpWithUsernameRequest;
import com.example.authservice.dto.response.LoginResponse;

import java.util.Map;

public interface IAuthenticationService {

    LoginResponse refreshToken(String refToken);

    Map<String, Object> logout(LogoutRequest logoutRequest);

    LoginResponse login(LoginByUsernameRequest loginRequest);

    Map<String, Object> signUpWithUsername(SignUpWithUsernameRequest request);

}
