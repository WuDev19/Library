package com.example.userservice.service.base;

import com.example.userservice.dto.request.UserCreateRequest;
import com.example.userservice.dto.response.UserCreateResponse;

public interface IUserService {
    UserCreateResponse createUser(UserCreateRequest request);
    void deleteUser(Long userId);
}
