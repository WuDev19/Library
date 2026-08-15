package com.example.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank(message = "Họ tên không được để trống")
        String fullName,

        @Size(min = 10, max = 11, message = "Số điện thoại phải từ 10-11 ký tự")
        String phone
) {
}
