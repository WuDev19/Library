package com.example.bookborrowservice.service.base;

import com.example.bookborrowservice.dto.request.BorrowRequest;
import com.example.bookborrowservice.dto.request.ReturnRequest;
import com.example.bookborrowservice.dto.response.BorrowRecordResponse;
import com.example.bookborrowservice.dto.response.BorrowedBookResponse;

import java.util.List;
import java.util.Map;

public interface IBorrowService {
    void borrowBook(BorrowRequest request, Long librarianId, Long authenticatedUserId);

    void returnBook(ReturnRequest request, Long librarianId);

    List<BorrowRecordResponse> getAllBorrowRecords();

    List<BorrowRecordResponse> getBorrowRecordsByUser(Long userId);

    BorrowRecordResponse getBorrowRecordById(Long id);

    Map<Long, List<BorrowedBookResponse>> getActiveBorrowsByUserIds(List<Long> userIds);
}
