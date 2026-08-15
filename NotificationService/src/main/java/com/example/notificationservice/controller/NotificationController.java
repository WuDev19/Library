package com.example.notificationservice.controller;

import com.example.notificationservice.constants.Constants;
import com.example.notificationservice.dto.common.ApiResponse;
import com.example.notificationservice.dto.common.ApiResult;
import com.example.notificationservice.dto.common.CRUDResponseHelper;
import com.example.notificationservice.dto.response.NotificationResponse;
import com.example.notificationservice.service.base.INotificationService;
import com.example.notificationservice.utils.StringCommon;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('LIBRARIAN', 'BORROWER')")
    public ResponseEntity<ApiResult<List<NotificationResponse>>> getMyNotifications(@AuthenticationPrincipal Jwt jwt) {
        Long authenticatedUserId = jwt.getClaim(StringCommon.USER_ID);
        List<NotificationResponse> notifications = notificationService.getNotificationsForUser(authenticatedUserId);
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
}
