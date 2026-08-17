package com.example.authservice.controller;

import com.example.authservice.constants.Constants;
import com.example.authservice.dto.common.ApiResponse;
import com.example.authservice.dto.common.ApiResult;
import com.example.authservice.dto.request.auth.LoginByUsernameRequest;
import com.example.authservice.dto.request.auth.LogoutRequest;
import com.example.authservice.dto.request.auth.SignUpWithUsernameRequest;
import com.example.authservice.dto.response.LoginResponse;
import com.example.authservice.service.base.IAuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Tài liệu API cho AuthController")
public class AuthController {

    private final IAuthenticationService iAuthenticationService;

    @Operation(summary = "Api cho người dùng đăng ký tài khoản")
    @PostMapping("/sign-up")
    public ResponseEntity<ApiResult<Map<String, Object>>> signUpWithUsername(@Valid @RequestBody SignUpWithUsernameRequest request) {
        return ApiResponse.success(
                iAuthenticationService.signUpWithUsername(request),
                "Đăng kí tài khoản thành công",
                Constants.SUCCESS_CODE
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Api cho người dùng đăng nhập")
    public ResponseEntity<ApiResult<LoginResponse>> login(@Valid @RequestBody LoginByUsernameRequest request) {
        return ApiResponse.success(
                iAuthenticationService.login(request),
                "Đăng nhập thành công",
                Constants.SUCCESS_CODE
        );
    }

    @Operation(summary = "Api đăng xuất", method = "Post Method")
    @PostMapping("/logout")
    public ResponseEntity<ApiResult<Map<String, Object>>> logout(@Valid @RequestBody LogoutRequest request) {
        return ApiResponse.success(
                iAuthenticationService.logout(request),
                "Đăng xuất thành công",
                Constants.SUCCESS_CODE
        );
    }

    @Operation(summary = "Api lấy token mới khi hết hạn token hiện tại")
    @PostMapping("/refresh-token/{refToken}")
    public ResponseEntity<ApiResult<LoginResponse>> refreshToken(@PathVariable String refToken) {
        return ApiResponse.success(
                iAuthenticationService.refreshToken(refToken),
                "Cấp lại access token và refresh token thành công",
                Constants.SUCCESS_CODE
        );
    }

}
