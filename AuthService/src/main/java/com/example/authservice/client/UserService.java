package com.example.authservice.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "UserService")
public interface UserService {


}
