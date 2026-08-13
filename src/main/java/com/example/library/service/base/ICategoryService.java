package com.example.library.service.base;

import com.example.library.dto.request.CategoryRequest;
import com.example.library.entity.Category;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryRequest request);
    Category updateCategory(Long id, CategoryRequest request);
    void deleteCategory(Long id);
    Category getCategoryById(Long id);
    List<Category> getAllCategories();
}
