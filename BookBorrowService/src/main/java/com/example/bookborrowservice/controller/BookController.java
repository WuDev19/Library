package com.example.bookborrowservice.controller;

import com.example.bookborrowservice.constants.Constants;
import com.example.bookborrowservice.constants.StringCommon;
import com.example.bookborrowservice.dto.common.ApiResponse;
import com.example.bookborrowservice.dto.common.ApiResult;
import com.example.bookborrowservice.dto.common.CRUDResponseHelper;
import com.example.bookborrowservice.dto.common.PageResponse;
import com.example.bookborrowservice.dto.request.BookImportRequest;
import com.example.bookborrowservice.dto.request.BookRequest;
import com.example.bookborrowservice.dto.response.BookResponse;
import com.example.bookborrowservice.service.base.IBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@SecurityRequirement(name = StringCommon.SECURITY_SCHEME)
@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final IBookService bookService;

    @Operation(summary = "Api cho librarian tạo đầu sách mới")
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<Map<String, Object>>> createBook(@Valid @RequestBody BookRequest request) {
        bookService.createBook(request);
        return ApiResponse.success(
                CRUDResponseHelper.createSuccess(),
                "Thêm mới sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @Operation(summary = "Api cho librarian cập nhật đầu sách")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<Map<String, Object>>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequest request
    ) {
        bookService.updateBook(id, request);
        return ApiResponse.success(
                CRUDResponseHelper.updateSuccess(),
                "Cập nhật thông tin sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @Operation(summary = "Api cho librarian xóa đầu sách")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<Map<String, Object>>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ApiResponse.success(
                CRUDResponseHelper.deleteSuccess(),
                "Xóa sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @Operation(summary = "Api cho user lấy đầu sách chi tiết")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<BookResponse>> getBookById(@PathVariable Long id) {
        BookResponse book = bookService.getBookById(id);
        return ApiResponse.success(
                book,
                "Lấy thông tin chi tiết sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @Operation(summary = "Api cho user lấy tất cả đầu sách")
    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<PageResponse<BookResponse>>> getAllBooks(@PageableDefault Pageable pageable) {
        PageResponse<BookResponse> books = bookService.getAllBooks(pageable);
        return ApiResponse.success(
                books,
                "Lấy danh sách sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @Operation(summary = "Api cho user tìm kiếm đầu sách")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<PageResponse<BookResponse>>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String code,
            @PageableDefault Pageable pageable
    ) {
        PageResponse<BookResponse> books = bookService.searchBooks(title, code, pageable);
        return ApiResponse.success(
                books,
                "Tìm kiếm sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @Operation(summary = "Api cho user tìm kiếm đầu sách theo danh mục")
    @GetMapping("/by-category/{categoryCode}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<PageResponse<BookResponse>>> getBooksByCategoryCode(@PathVariable String categoryCode,
                                                                                        @PageableDefault Pageable pageable) {
        PageResponse<BookResponse> books = bookService.getBooksByCategoryCode(categoryCode, pageable);
        return ApiResponse.success(
                books,
                "Lấy danh sách sách theo danh mục thành công",
                Constants.SUCCESS_CODE
        );
    }

    @Operation(summary = "Api cho librarian nhập thêm đầu sách mới")
    @PostMapping("/import")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<Map<String, Object>>> importBooks(
            @Valid @RequestBody BookImportRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long librarianId = jwt.getClaim("userId");
        bookService.importBooks(request, librarianId);
        return ApiResponse.success(
                CRUDResponseHelper.createSuccess(),
                "Nhập sách mới thành công",
                Constants.SUCCESS_CODE
        );
    }
}
