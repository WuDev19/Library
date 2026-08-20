package com.example.bookborrowservice.dto.common;

import java.util.List;

public record PageResponse<T>(
        int page,
        int sizeOfPage,
        List<T> content
) {
}
