package com.example.library.controller;

import com.example.library.constants.Constants;
import com.example.library.dto.common.ApiResponse;
import com.example.library.dto.common.ApiResult;
import com.example.library.dto.common.CRUDResponseHelper;
import com.example.library.entity.Notification;
import com.example.library.service.base.INotificationService;
import com.example.library.service.impl.LibraryNotificationJob;
import com.example.library.utils.StringCommon;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;
    private final LibraryNotificationJob notificationJob;

    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<List<Notification>>> getMyNotifications(@AuthenticationPrincipal Jwt jwt) {
        Long authenticatedUserId = jwt.getClaim(StringCommon.USER_ID);
        List<Notification> notifications = notificationService.getNotificationsForUser(authenticatedUserId);
        return ApiResponse.success(
                notifications,
                "Lấy danh sách thông báo thành công",
                Constants.SUCCESS_CODE
        );
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<Map<String, Object>>> markAsRead(
            @PathVariable Long id, 
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long authenticatedUserId = jwt.getClaim(StringCommon.USER_ID);
        notificationService.markAsRead(id, authenticatedUserId);
        return ApiResponse.success(
                CRUDResponseHelper.updateSuccess(),
                "Đánh dấu đã đọc thông báo thành công",
                Constants.SUCCESS_CODE
        );
    }

    @PostMapping("/trigger-scan")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public ResponseEntity<ApiResult<Map<String, Object>>> triggerScan() {
        notificationJob.runScan();
        return ApiResponse.success(
                CRUDResponseHelper.modifySuccess(),
                "Kích hoạt quét và gửi thông báo sách đến hạn/quá hạn thành công",
                Constants.SUCCESS_CODE
        );
    }
}
