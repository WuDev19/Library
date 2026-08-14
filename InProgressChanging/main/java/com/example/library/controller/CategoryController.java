package com.example.library.controller;

import com.example.library.constants.Constants;
import com.example.library.dto.common.ApiResponse;
import com.example.library.dto.common.ApiResult;
import com.example.library.dto.common.CRUDResponseHelper;
import com.example.library.dto.request.CategoryRequest;
import com.example.library.entity.Category;
import com.example.library.service.base.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<Map<String, Object>>> createCategory(@Valid @RequestBody CategoryRequest request) {
        categoryService.createCategory(request);
        return ApiResponse.success(
                CRUDResponseHelper.createSuccess(),
                "Tạo danh mục sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<Map<String, Object>>> updateCategory(
            @PathVariable Long id, 
            @Valid @RequestBody CategoryRequest request
    ) {
        categoryService.updateCategory(id, request);
        return ApiResponse.success(
                CRUDResponseHelper.updateSuccess(),
                "Cập nhật danh mục sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<Map<String, Object>>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ApiResponse.success(
                CRUDResponseHelper.deleteSuccess(),
                "Xóa danh mục sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<Category>> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ApiResponse.success(
                category,
                "Lấy thông tin danh mục sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ApiResponse.success(
                categories,
                "Lấy danh sách danh mục sách thành công",
                Constants.SUCCESS_CODE
        );
    }
}
