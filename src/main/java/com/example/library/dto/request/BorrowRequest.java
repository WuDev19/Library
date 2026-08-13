package com.example.library.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BorrowRequest(
    @NotNull(message = "ID sách không được để trống")
    Long bookId,

    Long borrowerId,

    @NotNull(message = "Hạn trả sách không được để trống")
    LocalDate dueDate,

    String note
) {
}
