package com.example.bookborrowservice.mapper;

import com.example.bookborrowservice.dto.request.CategoryRequest;
import com.example.bookborrowservice.dto.response.CategoryResponse;
import com.example.bookborrowservice.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category mapToEntity(CategoryRequest request) {
        return Category.builder()
                .code(request.code())
                .name(request.name())
                .description(request.description())
                .build();
    }

    public CategoryResponse mapToResponse(Category category) {
        if (category == null) return null;
        return new CategoryResponse(
                category.getCategoryId(),
                category.getCode(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
