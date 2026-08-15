package com.example.bookborrowservice.service.impl;

import com.example.bookborrowservice.dto.request.BorrowRequest;
import com.example.bookborrowservice.dto.request.ReturnRequest;
import com.example.bookborrowservice.dto.response.BorrowRecordResponse;
import com.example.bookborrowservice.dto.response.BorrowedBookResponse;
import com.example.bookborrowservice.entity.BookCopy;
import com.example.bookborrowservice.entity.BorrowRecord;
import com.example.bookborrowservice.entity.enums.BorrowStatus;
import com.example.bookborrowservice.entity.enums.CopyStatus;
import com.example.bookborrowservice.exception.BusinessException;
import com.example.bookborrowservice.exception.ErrorResponse;
import com.example.bookborrowservice.mapper.BorrowMapper;
import com.example.bookborrowservice.repository.BookCopyRepository;
import com.example.bookborrowservice.repository.BookRepository;
import com.example.bookborrowservice.repository.BorrowRecordRepository;
import com.example.bookborrowservice.service.base.IBorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements IBorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookRepository bookRepository;
    private final BorrowMapper borrowMapper;

    @Override
    @Transactional
    public void borrowBook(BorrowRequest request, Long librarianId, Long authenticatedUserId) {
        if (request.dueDate().isBefore(LocalDate.now())) {
            throw new BusinessException(ErrorResponse.DUE_DATE_INVALID);
        }

        Long targetBorrowerId = (librarianId != null && request.borrowerId() != null)
                ? request.borrowerId()
                : authenticatedUserId;

        if (!bookRepository.existsById(request.bookId())) {
            throw new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND);
        }

        List<BookCopy> availableCopies = bookCopyRepository.findByBookBookIdAndStatus(request.bookId(), CopyStatus.AVAILABLE);
        if (availableCopies.isEmpty()) {
            throw new BusinessException(ErrorResponse.BOOK_OUT_OF_STOCK);
        }

        BookCopy copyToBorrow = availableCopies.get(0);
        copyToBorrow.setStatus(CopyStatus.BORROWED);
        bookCopyRepository.save(copyToBorrow);

        String borrowCode = "BRW-" + System.currentTimeMillis();

        BorrowRecord record = BorrowRecord.builder()
                .borrowCode(borrowCode)
                .bookCopy(copyToBorrow)
                .borrowerId(targetBorrowerId)
                .librarianId(librarianId)
                .borrowDate(LocalDate.now())
                .dueDate(request.dueDate())
                .status(BorrowStatus.BORROWING)
                .note(request.note())
                .build();

        borrowRecordRepository.save(record);
    }

    @Override
    @Transactional
    public void returnBook(ReturnRequest request, Long librarianId) {
        BorrowRecord record = borrowRecordRepository.findByBorrowCodeWithDetails(request.borrowCode())
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));

        if (record.getStatus() != BorrowStatus.BORROWING && record.getStatus() != BorrowStatus.OVERDUE) {
            throw new BusinessException(ErrorResponse.BORROW_RECORD_NOT_ACTIVE);
        }

        BookCopy copy = record.getBookCopy();
        copy.setStatus(CopyStatus.AVAILABLE);
        bookCopyRepository.save(copy);

        record.setReturnDate(LocalDate.now());
        record.setStatus(BorrowStatus.RETURNED);
        record.setLibrarianId(librarianId);
        if (request.note() != null && !request.note().trim().isEmpty()) {
            record.setNote(record.getNote() != null
                    ? record.getNote() + " | Trả sách: " + request.note()
                    : "Trả sách: " + request.note());
        }

        borrowRecordRepository.save(record);
    }

    @Override
    public List<BorrowRecordResponse> getBorrowRecordsByUser(Long userId) {
        return borrowRecordRepository.findByBorrowerUserIdWithDetails(userId).stream()
                .map(borrowMapper::mapToResponse)
                .toList();
    }

    @Override
    public List<BorrowRecordResponse> getAllBorrowRecords() {
        return borrowRecordRepository.findAllWithDetails().stream()
                .map(borrowMapper::mapToResponse)
                .toList();
    }

    @Override
    public BorrowRecordResponse getBorrowRecordById(Long id) {
        BorrowRecord record = borrowRecordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        return borrowMapper.mapToResponse(record);
    }

    @Override
    public Map<Long, List<BorrowedBookResponse>> getActiveBorrowsByUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyMap();
        }

        List<BorrowRecord> activeRecords = borrowRecordRepository.findActiveBorrowsByUserIdsWithDetails(
                userIds,
                List.of(BorrowStatus.BORROWING, BorrowStatus.OVERDUE)
        );

        return activeRecords.stream()
                .collect(Collectors.groupingBy(
                        BorrowRecord::getBorrowerId,
                        Collectors.mapping(borrowMapper::mapToBorrowedBookResponse, Collectors.toList())
                ));
    }
}
