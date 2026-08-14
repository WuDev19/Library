package com.example.library.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReturnRequest(
    @NotBlank(message = "Mã mượn sách không được để trống")
    String borrowCode,

    String note
) {
}
