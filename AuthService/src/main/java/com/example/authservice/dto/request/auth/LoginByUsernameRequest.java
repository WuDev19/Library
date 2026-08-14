package com.example.authservice.dto.request.auth;

import com.example.authservice.constants.StringCommon;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginByUsernameRequest(
        @Size(min = 1, message = "Vui lòng nhập username")
        @NotNull
        String username,

        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Mật khẩu phải từ 8 ký tự trở lên, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt")
        @NotBlank(message = "Mật khẩu " + StringCommon.NOT_BLANK)
        @NotNull
        String password
) {
}
