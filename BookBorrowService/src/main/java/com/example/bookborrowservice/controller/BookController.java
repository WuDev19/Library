package com.example.bookborrowservice.controller;

import com.example.bookborrowservice.constants.Constants;
import com.example.bookborrowservice.dto.common.ApiResponse;
import com.example.bookborrowservice.dto.common.ApiResult;
import com.example.bookborrowservice.dto.common.CRUDResponseHelper;
import com.example.bookborrowservice.dto.request.BookImportRequest;
import com.example.bookborrowservice.dto.request.BookRequest;
import com.example.bookborrowservice.dto.response.BookResponse;
import com.example.bookborrowservice.service.base.IBookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final IBookService bookService;

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

    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<List<BookResponse>>> getAllBooks() {
        List<BookResponse> books = bookService.getAllBooks();
        return ApiResponse.success(
                books,
                "Lấy danh sách sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<List<BookResponse>>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String code
    ) {
        List<BookResponse> books = bookService.searchBooks(title, code);
        return ApiResponse.success(
                books,
                "Tìm kiếm sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @GetMapping("/by-category/{categoryCode}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<List<BookResponse>>> getBooksByCategoryCode(@PathVariable String categoryCode) {
        List<BookResponse> books = bookService.getBooksByCategoryCode(categoryCode);
        return ApiResponse.success(
                books,
                "Lấy danh sách sách theo danh mục thành công",
                Constants.SUCCESS_CODE
        );
    }

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
