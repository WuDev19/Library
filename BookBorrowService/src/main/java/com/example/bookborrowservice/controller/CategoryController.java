package com.example.bookborrowservice.controller;

import com.example.bookborrowservice.constants.Constants;
import com.example.bookborrowservice.constants.StringCommon;
import com.example.bookborrowservice.dto.common.ApiResponse;
import com.example.bookborrowservice.dto.common.ApiResult;
import com.example.bookborrowservice.dto.common.CRUDResponseHelper;
import com.example.bookborrowservice.dto.request.CategoryRequest;
import com.example.bookborrowservice.dto.response.CategoryResponse;
import com.example.bookborrowservice.service.base.ICategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SecurityRequirement(name = StringCommon.SECURITY_SCHEME)
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Tài liệu API cho danh mục sách")
public class CategoryController {

    private final ICategoryService categoryService;

    @Operation(summary = "Api cho librarian tạo danh mục sách")
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

    @Operation(summary = "Api cho librarian cập nhật danh mục sách")
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

    @Operation(summary = "Api cho librarian xóa danh mục sách")
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

    @Operation(summary = "Api cho user xem danh mục sách chi tiết")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<CategoryResponse>> getCategoryById(@PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ApiResponse.success(
                category,
                "Lấy thông tin danh mục sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @Operation(summary = "Api cho librarian xem toàn bộ danh mục sách")
    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<List<CategoryResponse>>> getAllCategories() {
        List<CategoryResponse> categories = categoryService.getAllCategories();
        return ApiResponse.success(
                categories,
                "Lấy danh sách danh mục sách thành công",
                Constants.SUCCESS_CODE
        );
    }
}
