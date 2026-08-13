package com.example.library.service.base;

import com.example.library.dto.request.BookImportRequest;
import com.example.library.dto.request.BookRequest;
import com.example.library.entity.Book;
import com.example.library.entity.BookImport;

import java.util.List;

public interface IBookService {
    Book createBook(BookRequest request);
    Book updateBook(Long id, BookRequest request);
    void deleteBook(Long id);
    Book getBookById(Long id);
    List<Book> getAllBooks();
    List<Book> searchBooks(String title, String code, String borrower);
    BookImport importBooks(BookImportRequest request, Long librarianId);
}
