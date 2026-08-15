package com.example.bookborrowservice.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BorrowRequest(
        @NotNull(message = "Mã ID sách không được để trống")
        Long bookId,

        @NotNull(message = "Hạn trả sách không được để trống")
        @FutureOrPresent(message = "Hạn trả sách phải là ngày hiện tại hoặc tương lai")
        LocalDate dueDate,

        Long borrowerId, // Nếu thủ thư đăng ký mượn hộ user khác
        String note
) {
}
