package com.example.userservice.exception;

import lombok.Getter;

@Getter
public enum ErrorResponse {
    DATA_INVALID("Dữ liệu gửi lên không hợp lệ, vui lòng xem lại các trường dữ liệu", 1000),
    NULL_POINTER("Dữ liệu trống", 1002),
    JWT_EXCEPTION("Phiên đăng nhập hết hạn, vui lòng đăng nhập lại", 1003),
    PARSE_JSON("Định dạng dữ liệu không hợp lệ", 1004),
    FIELD_INVALID("Trường dữ liệu gửi lên không hợp lệ", 1005),
    OBJECT_INVALID("", 1006),
    RESOURCE_NOT_FOUND("Dữ liệu không tồn tại", 1007),
    DATA_INTEGRITY("Xung đột dữ liệu", 1009),
    ACCESS_DENIED("Bạn không có quyền thực hiện thao tác này", 1020),
    FAKE_AUTH_ERROR("Bạn đang mạo danh người khác, nghiêm cấm hành vi này", 1021),
    INTERNAL_GRPC_EXCEPTION("Lỗi ko xác định gọi GRPC nội bộ", 1022);

    private final String message;
    private final int code;

    ErrorResponse(String message, int code) {
        this.message = message;
        this.code = code;
    }
}
