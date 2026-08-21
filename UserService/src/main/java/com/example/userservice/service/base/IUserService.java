package com.example.userservice.service.base;

import com.example.userservice.dto.common.PageResponse;
import com.example.userservice.dto.request.UserCreateRequest;
import com.example.userservice.dto.request.UserUpdateRequest;
import com.example.userservice.dto.response.UserCreateResponse;
import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.dto.response.UserSearchResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUserService {

    UserCreateResponse createUser(UserCreateRequest request);

    UserResponse getUserById(Long userId);

    UserResponse updateUser(Long userId, UserUpdateRequest request);

    void deleteUser(Long userId);

    PageResponse<UserSearchResponse> searchUsers(String keyword, Pageable pageable);

    PageResponse<UserSearchResponse> getAllUsers(Pageable pageable);
}
