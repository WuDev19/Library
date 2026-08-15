package com.example.bookborrowservice.mapper;

import com.example.bookborrowservice.dto.request.BookRequest;
import com.example.bookborrowservice.dto.response.BookCopyResponse;
import com.example.bookborrowservice.dto.response.BookImportResponse;
import com.example.bookborrowservice.dto.response.BookResponse;
import com.example.bookborrowservice.entity.Book;
import com.example.bookborrowservice.entity.BookCopy;
import com.example.bookborrowservice.entity.BookImport;
import com.example.bookborrowservice.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book mapToEntity(BookRequest request, Category category) {
        return Book.builder()
                .code(request.code())
                .title(request.title())
                .category(category)
                .author(request.author())
                .publisher(request.publisher())
                .publishedYear(request.publishedYear())
                .isbn(request.isbn())
                .description(request.description())
                .build();
    }

    public BookResponse mapToResponse(Book book) {
        if (book == null) return null;
        return new BookResponse(
                book.getBookId(),
                book.getCode(),
                book.getTitle(),
                book.getCategory() != null ? book.getCategory().getCategoryId() : null,
                book.getCategory() != null ? book.getCategory().getName() : null,
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishedYear(),
                book.getIsbn(),
                book.getDescription(),
                book.getTotalQuantity(),
                book.getAvailableQuantity(),
                book.getCreatedAt(),
                book.getUpdatedAt()
        );
    }

    public BookCopyResponse mapToCopyResponse(BookCopy copy) {
        if (copy == null) return null;
        return new BookCopyResponse(
                copy.getBookCopyId(),
                copy.getBook() != null ? copy.getBook().getBookId() : null,
                copy.getAssetCode(),
                copy.getStatus(),
                copy.getNote()
        );
    }

    public BookImportResponse mapToImportResponse(BookImport bookImport) {
        if (bookImport == null) return null;
        return new BookImportResponse(
                bookImport.getBookImportId(),
                bookImport.getImportCode(),
                bookImport.getBook() != null ? bookImport.getBook().getBookId() : null,
                bookImport.getBook() != null ? bookImport.getBook().getTitle() : null,
                bookImport.getQuantity(),
                bookImport.getImportedBy(),
                bookImport.getImportDate(),
                bookImport.getNote(),
                bookImport.getCreatedAt()
        );
    }
}
