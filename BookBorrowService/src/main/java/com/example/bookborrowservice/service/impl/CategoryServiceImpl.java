package com.example.bookborrowservice.service.impl;

import com.example.bookborrowservice.dto.request.CategoryRequest;
import com.example.bookborrowservice.dto.response.CategoryResponse;
import com.example.bookborrowservice.entity.Category;
import com.example.bookborrowservice.exception.BusinessException;
import com.example.bookborrowservice.exception.ErrorResponse;
import com.example.bookborrowservice.mapper.CategoryMapper;
import com.example.bookborrowservice.repository.CategoryRepository;
import com.example.bookborrowservice.service.base.ICategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public void createCategory(CategoryRequest request) {
        if (categoryRepository.existsByCode(request.code())) {
            throw new BusinessException(ErrorResponse.DATA_INTEGRITY);
        }
        Category category = categoryMapper.mapToEntity(request);
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void updateCategory(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        if (!category.getCode().equalsIgnoreCase(request.code()) && categoryRepository.existsByCode(request.code())) {
            throw new BusinessException(ErrorResponse.DATA_INTEGRITY);
        }
        category.setCode(request.code());
        category.setName(request.name());
        category.setDescription(request.description());
        categoryRepository.save(category);
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
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        return categoryMapper.mapToResponse(category);
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::mapToResponse)
                .toList();
    }
}
