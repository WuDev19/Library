package com.example.userservice.client;

import com.example.userservice.dto.response.BorrowedBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@FeignClient(name = "BookBorrowService", fallback = BookBorrowServiceImpl.class)
public interface BookBorrowService {

    @PostMapping("/borrows/active-by-users")
    Map<Long, List<BorrowedBookResponse>> getActiveBorrowsByUserIds(@RequestBody List<Long> userIds);
}

@Service
@Slf4j
class BookBorrowServiceImpl implements BookBorrowService {

    @Override
    public Map<Long, List<BorrowedBookResponse>> getActiveBorrowsByUserIds(List<Long> userIds) {
        log.error("[Feign Fallback] Không thể lấy thông tin mượn sách cho userIds={}", userIds);
        return Collections.emptyMap();
    }
}
