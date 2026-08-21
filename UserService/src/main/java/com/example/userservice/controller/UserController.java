package com.example.userservice.controller;

import com.example.userservice.constants.Constants;
import com.example.userservice.constants.StringCommon;
import com.example.userservice.dto.common.ApiResponse;
import com.example.userservice.dto.common.ApiResult;
import com.example.userservice.dto.common.CRUDResponseHelper;
import com.example.userservice.dto.common.PageResponse;
import com.example.userservice.dto.request.UserCreateRequest;
import com.example.userservice.dto.request.UserUpdateRequest;
import com.example.userservice.dto.response.UserCreateResponse;
import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.dto.response.UserSearchResponse;
import com.example.userservice.service.base.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SecurityRequirement(name = StringCommon.SECURITY_SCHEME)
@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @Operation(summary = "Api cho user tạo tài khoản và thông tin")
    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM', 'LIBRARIAN')")
    public UserCreateResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @Operation(summary = "Api cho librarian xóa tài khoản user")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SYSTEM', 'LIBRARIAN')")
    public void deleteUserInternal(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @Operation(summary = "Api cho user lấy thông tin chi tiết")
    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SYSTEM', 'LIBRARIAN') or (hasRole('BORROWER') and #userId == authentication.token.claims['userId'])")
    public ResponseEntity<ApiResult<UserResponse>> getUserById(@PathVariable Long userId) {
        UserResponse response = userService.getUserById(userId);
        return ApiResponse.success(response, "Lấy thông tin user thành công", Constants.SUCCESS_CODE);
    }

    @Operation(summary = "Api cho user cập nhật tài khoản")
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('LIBRARIAN') or (hasRole('BORROWER') and #userId == authentication.token.claims['userId'])")
    public ResponseEntity<ApiResult<UserResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse response = userService.updateUser(userId, request);
        return ApiResponse.success(response, "Cập nhật thông tin user thành công", Constants.SUCCESS_CODE);
    }

    @Operation(summary = "Api cho librarian tìm kiếm user")
    @GetMapping("/search")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<PageResponse<UserSearchResponse>>> searchUsers(
            @RequestParam(required = false) String keyword,
            @PageableDefault Pageable pageable
    ) {
        PageResponse<UserSearchResponse> results = userService.searchUsers(keyword, pageable);
        return ApiResponse.success(results, "Tìm kiếm user thành công", Constants.SUCCESS_CODE);
    }

    @Operation(summary = "Api cho librarian lấy tất cả user")
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<PageResponse<UserSearchResponse>>> getAllUsers(
            @PageableDefault Pageable pageable
    ) {
        PageResponse<UserSearchResponse> results = userService.getAllUsers(pageable);
        return ApiResponse.success(results, "Lấy danh sách user thành công", Constants.SUCCESS_CODE);
    }

    @Operation(summary = "Api cho librarian xóa user")
    @DeleteMapping("/admin/{userId}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<Map<String, Object>>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ApiResponse.success(
                CRUDResponseHelper.deleteSuccess(),
                "Xóa user thành công",
                Constants.SUCCESS_CODE
        );
    }
}
