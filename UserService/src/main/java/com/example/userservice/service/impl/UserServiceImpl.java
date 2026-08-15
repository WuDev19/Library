package com.example.userservice.service.impl;

import com.example.userservice.client.BookBorrowService;
import com.example.userservice.dto.request.UserCreateRequest;
import com.example.userservice.dto.request.UserUpdateRequest;
import com.example.userservice.dto.response.BorrowedBookResponse;
import com.example.userservice.dto.response.UserCreateResponse;
import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.dto.response.UserSearchResponse;
import com.example.userservice.entity.User;
import com.example.userservice.exception.BusinessException;
import com.example.userservice.exception.ErrorResponse;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import com.example.userservice.service.base.IUserService;
import com.example.userservice.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BookBorrowService bookBorrowService;

    @Transactional
    @Override
    public UserCreateResponse createUser(UserCreateRequest request) {
        if(request != null){
            throw new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND);
        }
        User user = User.builder()
                .userId(request.userId())
                .email(request.email())
                .fullName(request.fullName())
                .phone(request.phone())
                .build();
        userRepository.save(user);
        return userMapper.mapToUserResponse(request);
    }

    @Override
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        return userMapper.mapToUserResponse(user);
    }

    @Transactional
    @Override
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        user.setFullName(request.fullName());
        if (request.phone() != null) {
            user.setPhone(request.phone());
        }
        User saved = userRepository.save(user);
        return userMapper.mapToUserResponse(saved);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        user.setDeletedAt(TimeUtils.now());
        userRepository.save(user);
    }

    @Override
    public List<UserSearchResponse> searchUsers(String keyword) {
        List<User> users = (keyword == null || keyword.isBlank())
                ? userRepository.findAllActive()
                : userRepository.searchByKeyword(keyword.trim());

        return buildUserSearchResponses(users);
    }

    @Override
    public List<UserSearchResponse> getAllUsers() {
        List<User> users = userRepository.findAllActive();
        return buildUserSearchResponses(users);
    }

    private List<UserSearchResponse> buildUserSearchResponses(List<User> users) {
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> userIds = users.stream().map(User::getUserId).toList();
        Map<Long, List<BorrowedBookResponse>> activeBorrowsMap = bookBorrowService.getActiveBorrowsByUserIds(userIds);
        return users.stream()
                .map(user -> {
                    List<BorrowedBookResponse> books = activeBorrowsMap != null
                            ? activeBorrowsMap.getOrDefault(user.getUserId(), Collections.emptyList())
                            : Collections.emptyList();
                    int count = books.size();
                    return userMapper.mapToUserSearchResponse(user, count, books);
                })
                .toList();
    }
}
