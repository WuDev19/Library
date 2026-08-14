package com.example.library.service.base;

import com.example.library.dto.request.BorrowRequest;
import com.example.library.dto.request.ReturnRequest;
import com.example.library.entity.BorrowRecord;

import java.util.List;

public interface IBorrowService {
    BorrowRecord borrowBook(BorrowRequest request, Long librarianId, Long authenticatedUserId);
    BorrowRecord returnBook(ReturnRequest request, Long librarianId);
    List<BorrowRecord> getBorrowRecordsByUser(Long userId);
    List<BorrowRecord> getAllBorrowRecords();
    BorrowRecord getBorrowRecordById(Long id);
}
