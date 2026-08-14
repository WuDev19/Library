package com.example.userservice.mapper;

import com.example.userservice.dto.request.UserCreateRequest;
import com.example.userservice.dto.response.UserCreateResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserCreateResponse mapToUserResponse(UserCreateRequest request) {
        return new UserCreateResponse(request.userId(), request.fullName(), request.email(), request.phone());
    }
}
