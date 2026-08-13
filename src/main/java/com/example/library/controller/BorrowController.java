package com.example.library.controller;

import com.example.library.constants.Constants;
import com.example.library.dto.common.ApiResponse;
import com.example.library.dto.common.ApiResult;
import com.example.library.dto.common.CRUDResponseHelper;
import com.example.library.dto.request.BorrowRequest;
import com.example.library.dto.request.ReturnRequest;
import com.example.library.entity.BorrowRecord;
import com.example.library.exception.BusinessException;
import com.example.library.exception.ErrorResponse;
import com.example.library.service.base.IBorrowService;
import com.example.library.utils.StringCommon;
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
@RequestMapping("/api/v1/borrows")
@RequiredArgsConstructor
public class BorrowController {

    private final IBorrowService borrowService;

    @PostMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<Map<String, Object>>> borrowBook(
            @Valid @RequestBody BorrowRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long authenticatedUserId = jwt.getClaim(StringCommon.USER_ID);
        String roleStr = jwt.getClaimAsString(StringCommon.ROLES);
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
        Long librarianId = jwt.getClaim(StringCommon.USER_ID);
        borrowService.returnBook(request, librarianId);
        return ApiResponse.success(
                CRUDResponseHelper.updateSuccess(),
                "Trả sách thành công",
                Constants.SUCCESS_CODE
        );
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<List<BorrowRecord>>> getBorrowRecords(@AuthenticationPrincipal Jwt jwt) {
        Long authenticatedUserId = jwt.getClaim(StringCommon.USER_ID);
        String roleStr = jwt.getClaimAsString(StringCommon.ROLES);
        boolean isLibrarian = "LIBRARIAN".equalsIgnoreCase(roleStr);
        List<BorrowRecord> records;
        if (isLibrarian) {
            records = borrowService.getAllBorrowRecords();
        } else {
            records = borrowService.getBorrowRecordsByUser(authenticatedUserId);
        }
        return ApiResponse.success(
                records,
                "Lấy danh sách phiếu mượn thành công",
                Constants.SUCCESS_CODE
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<BorrowRecord>> getBorrowRecordById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long authenticatedUserId = jwt.getClaim(StringCommon.USER_ID);
        String roleStr = jwt.getClaimAsString(StringCommon.ROLES);
        boolean isLibrarian = "LIBRARIAN".equalsIgnoreCase(roleStr);
        BorrowRecord record = borrowService.getBorrowRecordById(id);
        if (!isLibrarian && !record.getBorrower().getUserId().equals(authenticatedUserId)) {
            throw new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND);
        }
        return ApiResponse.success(
                record,
                "Lấy chi tiết phiếu mượn thành công",
                Constants.SUCCESS_CODE
        );
    }
}
