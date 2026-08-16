package com.example.notificationservice.client;

import com.example.notificationservice.dto.common.ApiResult;
import com.example.notificationservice.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "UserService")
public interface UserService {

    @GetMapping("/{userId}")
    ApiResult<UserResponse> getUserById(@PathVariable Long userId);
}
