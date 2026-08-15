package com.example.userservice.controller;

import com.example.userservice.constants.Constants;
import com.example.userservice.dto.common.ApiResponse;
import com.example.userservice.dto.common.ApiResult;
import com.example.userservice.dto.common.CRUDResponseHelper;
import com.example.userservice.dto.request.UserCreateRequest;
import com.example.userservice.dto.request.UserUpdateRequest;
import com.example.userservice.dto.response.UserCreateResponse;
import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.dto.response.UserSearchResponse;
import com.example.userservice.service.base.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM', 'LIBRARIAN')")
    public UserCreateResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SYSTEM', 'LIBRARIAN')")
    public void deleteUserInternal(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('LIBRARIAN') or (hasRole('BORROWER') and #userId == authentication.token.claims['userId'])")
    public ResponseEntity<ApiResult<UserResponse>> getUserById(@PathVariable Long userId) {
        UserResponse response = userService.getUserById(userId);
        return ApiResponse.success(response, "Lấy thông tin user thành công", Constants.SUCCESS_CODE);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('LIBRARIAN') or (hasRole('BORROWER') and #userId == authentication.token.claims['userId'])")
    public ResponseEntity<ApiResult<UserResponse>> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserResponse response = userService.updateUser(userId, request);
        return ApiResponse.success(response, "Cập nhật thông tin user thành công", Constants.SUCCESS_CODE);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<List<UserSearchResponse>>> searchUsers(
            @RequestParam(required = false) String keyword
    ) {
        List<UserSearchResponse> results = userService.searchUsers(keyword);
        return ApiResponse.success(results, "Tìm kiếm user thành công", Constants.SUCCESS_CODE);
    }

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<List<UserSearchResponse>>> getAllUsers() {
        List<UserSearchResponse> results = userService.getAllUsers();
        return ApiResponse.success(results, "Lấy danh sách user thành công", Constants.SUCCESS_CODE);
    }

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
