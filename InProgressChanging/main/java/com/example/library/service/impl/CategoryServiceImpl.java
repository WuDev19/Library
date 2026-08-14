package com.example.library.service.impl;

import com.example.library.dto.request.CategoryRequest;
import com.example.library.entity.Category;
import com.example.library.exception.BusinessException;
import com.example.library.exception.ErrorResponse;
import com.example.library.repository.CategoryRepository;
import com.example.library.service.base.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category createCategory(CategoryRequest request) {
        if (categoryRepository.existsByCode(request.code())) {
            throw new BusinessException(ErrorResponse.DATA_INTEGRITY);
        }
        Category category = Category.builder()
                .code(request.code())
                .name(request.name())
                .description(request.description())
                .build();
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));

        // Check code duplicate if code changes
        if (!category.getCode().equalsIgnoreCase(request.code()) && categoryRepository.existsByCode(request.code())) {
            throw new BusinessException(ErrorResponse.DATA_INTEGRITY);
        }

        category.setCode(request.code());
        category.setName(request.name());
        category.setDescription(request.description());
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND);
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
