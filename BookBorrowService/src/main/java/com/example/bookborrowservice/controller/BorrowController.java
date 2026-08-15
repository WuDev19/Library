package com.example.bookborrowservice.controller;

import com.example.bookborrowservice.constants.Constants;
import com.example.bookborrowservice.dto.common.ApiResponse;
import com.example.bookborrowservice.dto.common.ApiResult;
import com.example.bookborrowservice.dto.common.CRUDResponseHelper;
import com.example.bookborrowservice.dto.request.BorrowRequest;
import com.example.bookborrowservice.dto.request.ReturnRequest;
import com.example.bookborrowservice.dto.response.BorrowRecordResponse;
import com.example.bookborrowservice.dto.response.BorrowedBookResponse;
import com.example.bookborrowservice.exception.BusinessException;
import com.example.bookborrowservice.exception.ErrorResponse;
import com.example.bookborrowservice.service.base.IBorrowService;
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
@RequestMapping("/borrows")
@RequiredArgsConstructor
public class BorrowController {

    private final IBorrowService borrowService;

    @PostMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<Map<String, Object>>> borrowBook(
            @Valid @RequestBody BorrowRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long authenticatedUserId = jwt.getClaim("userId");
        String roleStr = jwt.getClaimAsString("roles");
        boolean isLibrarian = "LIBRARIAN".equalsIgnoreCase(roleStr);
        Long librarianId = isLibrarian ? authenticatedUserId : null;

        borrowService.borrowBook(request, librarianId, authenticatedUserId);

        return ApiResponse.success(
                CRUDResponseHelper.createSuccess(),
                "Đăng ký mượn sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @PostMapping("/return")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<Map<String, Object>>> returnBook(
            @Valid @RequestBody ReturnRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long librarianId = jwt.getClaim("userId");
        borrowService.returnBook(request, librarianId);
        return ApiResponse.success(
                CRUDResponseHelper.updateSuccess(),
                "Trả sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<List<BorrowRecordResponse>>> getBorrowRecords(@AuthenticationPrincipal Jwt jwt) {
        Long authenticatedUserId = jwt.getClaim("userId");
        String roleStr = jwt.getClaimAsString("roles");
        boolean isLibrarian = "LIBRARIAN".equalsIgnoreCase(roleStr);

        List<BorrowRecordResponse> records = isLibrarian
                ? borrowService.getAllBorrowRecords()
                : borrowService.getBorrowRecordsByUser(authenticatedUserId);

        return ApiResponse.success(
                records,
                "Lấy danh sách phiếu mượn thành công",
                Constants.SUCCESS_CODE
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<BorrowRecordResponse>> getBorrowRecordById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long authenticatedUserId = jwt.getClaim("userId");
        String roleStr = jwt.getClaimAsString("roles");
        boolean isLibrarian = "LIBRARIAN".equalsIgnoreCase(roleStr);

        BorrowRecordResponse record = borrowService.getBorrowRecordById(id);
        if (!isLibrarian && !record.borrowerId().equals(authenticatedUserId)) {
            throw new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND);
        }

        return ApiResponse.success(
                record,
                "Lấy chi tiết phiếu mượn thành công",
                Constants.SUCCESS_CODE
        );
    }

    @PostMapping("/active-by-users")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'SYSTEM')")
    public Map<Long, List<BorrowedBookResponse>> getActiveBorrowsByUserIds(@RequestBody List<Long> userIds) {
        return borrowService.getActiveBorrowsByUserIds(userIds);
    }

    @PostMapping("/scan-overdue")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<Map<String, Object>>> scanOverdueBorrows() {
        Map<String, Object> result = borrowService.scanOverdueBorrows();
        return ApiResponse.success(
                result,
                "Quét danh sách phiếu mượn quá hạn thành công",
                Constants.SUCCESS_CODE
        );
    }
}
