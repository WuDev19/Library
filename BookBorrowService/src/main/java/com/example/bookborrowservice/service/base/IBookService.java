package com.example.bookborrowservice.service.base;

import com.example.bookborrowservice.dto.common.PageResponse;
import com.example.bookborrowservice.dto.request.BookImportRequest;
import com.example.bookborrowservice.dto.request.BookRequest;
import com.example.bookborrowservice.dto.response.BookResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IBookService {
    void createBook(BookRequest request);

    void updateBook(Long id, BookRequest request);

    void deleteBook(Long id);

    BookResponse getBookById(Long id);

    PageResponse<BookResponse> getAllBooks(Pageable pageable);

    PageResponse<BookResponse> searchBooks(String title, String code, Pageable pageable);

    PageResponse<BookResponse> getBooksByCategoryCode(String categoryCode, Pageable pageable);

    void importBooks(BookImportRequest request, Long librarianId);
}
