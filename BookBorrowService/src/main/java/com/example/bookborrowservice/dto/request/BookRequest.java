package com.example.bookborrowservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BookRequest(
        @NotBlank(message = "Mã sách không được để trống")
        @Size(max = 30, message = "Mã sách tối đa 30 ký tự")
        String code,

        @NotBlank(message = "Tên sách không được để trống")
        @Size(max = 255, message = "Tên sách tối đa 255 ký tự")
        String title,

        @NotNull(message = "Danh mục không được để trống")
        Long categoryId,

        String author,
        String publisher,
        Short publishedYear,
        String isbn,
        String description
) {
}
