package com.example.library.service.impl;

import com.example.library.dto.request.BorrowRequest;
import com.example.library.dto.request.ReturnRequest;
import com.example.library.entity.*;
import com.example.library.entity.enums.BorrowStatus;
import com.example.library.entity.enums.CopyStatus;
import com.example.library.exception.BusinessException;
import com.example.library.exception.ErrorResponse;
import com.example.library.repository.BookCopyRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BorrowRecordRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.base.IBorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements IBorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BorrowRecord borrowBook(BorrowRequest request, Long librarianId, Long authenticatedUserId) {
        if (request.dueDate().isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorResponse.DUE_DATE_INVALID);
        }

        // Determine borrower
        Long targetBorrowerId = authenticatedUserId;
        if (librarianId != null && request.borrowerId() != null) {
            targetBorrowerId = request.borrowerId();
        }

        User borrower = userRepository.findById(targetBorrowerId)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));

        User librarian = null;
        if (librarianId != null) {
            librarian = userRepository.findById(librarianId).orElse(null);
        }

        // Ensure book exists
        if (!bookRepository.existsById(request.bookId())) {
            throw new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND);
        }

        // Find an AVAILABLE copy
        List<BookCopy> availableCopies = bookCopyRepository.findByBookBookIdAndStatus(request.bookId(), CopyStatus.AVAILABLE);
        if (availableCopies.isEmpty()) {
            throw new BusinessException(ErrorResponse.BOOK_OUT_OF_STOCK);
        }

        BookCopy copyToBorrow = availableCopies.get(0);
        copyToBorrow.setStatus(CopyStatus.BORROWED);
        bookCopyRepository.save(copyToBorrow);

        // Generate a unique borrow code
        String borrowCode = "BRW-" + System.currentTimeMillis();

        BorrowRecord record = BorrowRecord.builder()
                .borrowCode(borrowCode)
                .bookCopy(copyToBorrow)
                .borrower(borrower)
                .librarian(librarian)
                .borrowDate(LocalDate.now())
                .dueDate(request.dueDate())
                .status(BorrowStatus.BORROWING)
                .note(request.note())
                .build();

        return borrowRecordRepository.save(record);
    }

    @Override
    @Transactional
    public BorrowRecord returnBook(ReturnRequest request, Long librarianId) {
        BorrowRecord record = borrowRecordRepository.findByBorrowCode(request.borrowCode())
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));

        if (record.getStatus() != BorrowStatus.BORROWING && record.getStatus() != BorrowStatus.OVERDUE) {
            throw new BusinessException(ErrorResponse.BORROW_RECORD_NOT_ACTIVE);
        }

        User librarian = null;
        if (librarianId != null) {
            librarian = userRepository.findById(librarianId).orElse(null);
        }

        // Return copy back to AVAILABLE
        BookCopy copy = record.getBookCopy();
        copy.setStatus(CopyStatus.AVAILABLE);
        bookCopyRepository.save(copy);

        // Update borrow record status
        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowStatus.RETURNED);
        record.setLibrarian(librarian);
        if (request.note() != null && !request.note().trim().isEmpty()) {
            record.setNote(record.getNote() != null 
                    ? record.getNote() + " | Trả sách: " + request.note() 
                    : "Trả sách: " + request.note());
        }

        return borrowRecordRepository.save(record);
    }

    @Override
    public List<BorrowRecord> getBorrowRecordsByUser(Long userId) {
        return borrowRecordRepository.findByBorrowerUserId(userId);
    }

    @Override
    public List<BorrowRecord> getAllBorrowRecords() {
        return borrowRecordRepository.findAll();
    }

    @Override
    public BorrowRecord getBorrowRecordById(Long id) {
        return borrowRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
    }
}
