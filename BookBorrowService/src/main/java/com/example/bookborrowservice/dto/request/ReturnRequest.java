package com.example.bookborrowservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record ReturnRequest(
        @NotBlank(message = "Mã phiếu mượn không được để trống")
        String borrowCode,

        String note
) {
}
