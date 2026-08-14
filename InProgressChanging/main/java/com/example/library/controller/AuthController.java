package com.example.library.controller;

import com.example.library.constants.Constants;
import com.example.library.dto.common.ApiResponse;
import com.example.library.dto.common.ApiResult;
import com.example.library.dto.request.auth.LoginByUsernameRequest;
import com.example.library.dto.request.auth.LogoutRequest;
import com.example.library.dto.request.auth.SignUpWithUsernameRequest;
import com.example.library.dto.response.LoginResponse;
import com.example.library.service.base.IAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
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
