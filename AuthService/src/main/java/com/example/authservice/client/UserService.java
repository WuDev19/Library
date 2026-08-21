package com.example.authservice.client;

import com.example.authservice.dto.request.auth.UserCreateRequest;
import com.example.authservice.dto.response.UserCreateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@FeignClient(name = "UserService", fallback = UserServiceImpl.class)
public interface UserService {

    @PostMapping
    UserCreateResponse createUser(@RequestBody UserCreateRequest request);
}

@Service
@Slf4j
class UserServiceImpl implements UserService {

    @Override
    public UserCreateResponse createUser(UserCreateRequest request) {
        log.error("Lỗi gọi API");
        return null;
    }

}
