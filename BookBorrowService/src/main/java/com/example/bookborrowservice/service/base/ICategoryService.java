package com.example.bookborrowservice.service.base;

import com.example.bookborrowservice.dto.request.CategoryRequest;
import com.example.bookborrowservice.dto.response.CategoryResponse;

import java.util.List;

public interface ICategoryService {
    void createCategory(CategoryRequest request);

    void updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);

    CategoryResponse getCategoryById(Long id);

    List<CategoryResponse> getAllCategories();
}
