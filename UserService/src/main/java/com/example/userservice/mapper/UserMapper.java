package com.example.userservice.mapper;

import com.example.userservice.dto.request.UserCreateRequest;
import com.example.userservice.dto.response.BorrowedBookResponse;
import com.example.userservice.dto.response.UserCreateResponse;
import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.dto.response.UserSearchResponse;
import com.example.userservice.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public UserCreateResponse mapToUserResponse(UserCreateRequest request) {
        return new UserCreateResponse(request.userId(), request.fullName(), request.email(), request.phone());
    }

    public UserResponse mapToUserResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public UserSearchResponse mapToUserSearchResponse(User user, int borrowingCount, List<BorrowedBookResponse> books) {
        return new UserSearchResponse(
                user.getUserId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                borrowingCount,
                books
        );
    }
}
