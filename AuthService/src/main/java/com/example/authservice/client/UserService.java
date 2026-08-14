package com.example.authservice.client;

import com.example.authservice.dto.common.ApiResult;
import com.example.authservice.dto.request.auth.UserCreateRequest;
import com.example.authservice.dto.response.UserCreateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "UserService", fallback = UserServiceImpl.class)
public interface UserService {

    @PostMapping
    UserCreateResponse createUser(@RequestBody UserCreateRequest request);

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable Long userId);
}

@Slf4j
class UserServiceImpl implements UserService {

    @Override
    public UserCreateResponse createUser(UserCreateRequest request) {
        return null;
    }

    @Override
    public void deleteUser(Long userId) {
        log.error("Lỗi delete");
    }

}
