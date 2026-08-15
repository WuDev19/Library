package com.example.bookborrowservice.service.base;

import com.example.bookborrowservice.dto.request.BookImportRequest;
import com.example.bookborrowservice.dto.request.BookRequest;
import com.example.bookborrowservice.dto.response.BookResponse;

import java.util.List;

public interface IBookService {
    void createBook(BookRequest request);

    void updateBook(Long id, BookRequest request);

    void deleteBook(Long id);

    BookResponse getBookById(Long id);

    List<BookResponse> getAllBooks();

    List<BookResponse> searchBooks(String title, String code);

    List<BookResponse> getBooksByCategoryCode(String categoryCode);

    void importBooks(BookImportRequest request, Long librarianId);
}
