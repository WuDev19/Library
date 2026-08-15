package com.example.bookborrowservice.mapper;

import com.example.bookborrowservice.dto.response.BorrowRecordResponse;
import com.example.bookborrowservice.dto.response.BorrowedBookResponse;
import com.example.bookborrowservice.entity.BorrowRecord;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class BorrowMapper {

    public BorrowRecordResponse mapToResponse(BorrowRecord record) {
        if (record == null) return null;
        return new BorrowRecordResponse(
                record.getBorrowRecordId(),
                record.getBorrowCode(),
                record.getBookCopy() != null ? record.getBookCopy().getBookCopyId() : null,
                (record.getBookCopy() != null && record.getBookCopy().getBook() != null) ? record.getBookCopy().getBook().getBookId() : null,
                (record.getBookCopy() != null && record.getBookCopy().getBook() != null) ? record.getBookCopy().getBook().getTitle() : null,
                record.getBookCopy() != null ? record.getBookCopy().getAssetCode() : null,
                record.getBorrowerId(),
                record.getLibrarianId(),
                record.getBorrowDate(),
                record.getDueDate(),
                record.getReturnDate(),
                record.getStatus(),
                record.getNote(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }

    public BorrowedBookResponse mapToBorrowedBookResponse(BorrowRecord record) {
        if (record == null) return null;
        return new BorrowedBookResponse(
                record.getBorrowRecordId(),
                (record.getBookCopy() != null && record.getBookCopy().getBook() != null) ? record.getBookCopy().getBook().getBookId() : null,
                (record.getBookCopy() != null && record.getBookCopy().getBook() != null) ? record.getBookCopy().getBook().getTitle() : null,
                record.getBorrowDate() != null ? record.getBorrowDate().atStartOfDay().atOffset(ZoneOffset.of("+07:00")) : null,
                record.getDueDate() != null ? record.getDueDate().atStartOfDay().atOffset(ZoneOffset.of("+07:00")) : null
        );
    }
}
