package com.example.authservice.controller;

import com.example.authservice.constants.Constants;
import com.example.authservice.dto.common.ApiResponse;
import com.example.authservice.dto.common.ApiResult;
import com.example.authservice.dto.request.auth.LoginByUsernameRequest;
import com.example.authservice.dto.request.auth.LogoutRequest;
import com.example.authservice.dto.request.auth.SignUpWithUsernameRequest;
import com.example.authservice.dto.response.LoginResponse;
import com.example.authservice.service.base.IAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final IAuthenticationService iAuthenticationService;

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResult<Map<String, Object>>> signUpWithUsername(@Valid @RequestBody SignUpWithUsernameRequest request) {
        return ApiResponse.success(
                iAuthenticationService.signUpWithUsername(request),
                "Đăng kí tài khoản thành công",
                Constants.SUCCESS_CODE
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResult<LoginResponse>> login(@Valid @RequestBody LoginByUsernameRequest request) {
        return ApiResponse.success(
                iAuthenticationService.login(request),
                "Đăng nhập thành công",
                Constants.SUCCESS_CODE
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Map<String, Object>>> logout(@Valid @RequestBody LogoutRequest request) {
        return ApiResponse.success(
                iAuthenticationService.logout(request),
                "Đăng xuất thành công",
                Constants.SUCCESS_CODE
        );
    }

    @PostMapping("/refresh-token/{refToken}")
    public ResponseEntity<ApiResult<LoginResponse>> refreshToken(@PathVariable String refToken) {
        return ApiResponse.success(
                iAuthenticationService.refreshToken(refToken),
                "Cấp lại access token và refresh token thành công",
                Constants.SUCCESS_CODE
        );
    }

}
