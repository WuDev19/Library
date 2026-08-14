package com.example.library.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
    @NotBlank(message = "Mã danh mục không được để trống")
    @Size(max = 30, message = "Mã danh mục tối đa 30 ký tự")
    String code,

    @NotBlank(message = "Tên danh mục không được để trống")
    @Size(max = 150, message = "Tên danh mục tối đa 150 ký tự")
    String name,

    String description
) {
}
