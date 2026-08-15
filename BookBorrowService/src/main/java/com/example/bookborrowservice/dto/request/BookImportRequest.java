package com.example.bookborrowservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookImportRequest(
        @NotNull(message = "Mã ID sách không được để trống")
        Long bookId,

        @NotNull(message = "Số lượng nhập không được để trống")
        @Min(value = 1, message = "Số lượng nhập phải lớn hơn 0")
        Integer quantity,

        String note
) {
}
