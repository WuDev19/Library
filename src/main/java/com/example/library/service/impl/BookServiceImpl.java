package com.example.library.service.impl;

import com.example.library.dto.request.BookImportRequest;
import com.example.library.dto.request.BookRequest;
import com.example.library.entity.*;
import com.example.library.entity.enums.CopyStatus;
import com.example.library.exception.BusinessException;
import com.example.library.exception.ErrorResponse;
import com.example.library.repository.*;
import com.example.library.service.base.IBookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements IBookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BookCopyRepository bookCopyRepository;
    private final BookImportRepository bookImportRepository;
    private final BookImportItemRepository bookImportItemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Book createBook(BookRequest request) {
        if (bookRepository.existsByCode(request.code())) {
            throw new BusinessException(ErrorResponse.DATA_INTEGRITY);
        }
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));

        Book book = Book.builder()
                .code(request.code())
                .title(request.title())
                .category(category)
                .author(request.author())
                .publisher(request.publisher())
                .publishedYear(request.publishedYear())
                .isbn(request.isbn())
                .description(request.description())
                .build();
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));

        if (!book.getCode().equalsIgnoreCase(request.code()) && bookRepository.existsByCode(request.code())) {
            throw new BusinessException(ErrorResponse.DATA_INTEGRITY);
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));

        book.setCode(request.code());
        book.setTitle(request.title());
        book.setCategory(category);
        book.setAuthor(request.author());
        book.setPublisher(request.publisher());
        book.setPublishedYear(request.publishedYear());
        book.setIsbn(request.isbn());
        book.setDescription(request.description());

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND);
        }
        bookRepository.deleteById(id);
    }

    @Override
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
    }

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Override
    public List<Book> searchBooks(String title, String code, String borrower) {
        String searchTitle = (title != null && !title.trim().isEmpty()) ? title.trim() : null;
        String searchCode = (code != null && !code.trim().isEmpty()) ? code.trim() : null;
        String searchBorrower = (borrower != null && !borrower.trim().isEmpty()) ? borrower.trim() : null;

        return bookRepository.searchBooks(searchTitle, searchCode, searchBorrower);
    }

    @Override
    @Transactional
    public BookImport importBooks(BookImportRequest request, Long librarianId) {
        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        User librarian = userRepository.findById(librarianId)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        String importCode = "IMP-" + System.currentTimeMillis();
        BookImport bookImport = BookImport.builder()
                .importCode(importCode)
                .book(book)
                .quantity(request.quantity())
                .importedBy(librarian)
                .importDate(LocalDate.now())
                .note(request.note())
                .build();
        bookImport = bookImportRepository.save(bookImport);
        for (int i = 0; i < request.quantity(); i++) {
            String assetCode = book.getCode() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            BookCopy bookCopy = BookCopy.builder()
                    .book(book)
                    .assetCode(assetCode)
                    .status(CopyStatus.AVAILABLE)
                    .note("Imported via " + importCode)
                    .build();
            bookCopy = bookCopyRepository.save(bookCopy);
            BookImportItem item = BookImportItem.builder()
                    .bookImport(bookImport)
                    .bookCopy(bookCopy)
                    .build();
            bookImportItemRepository.save(item);
        }
        return bookImport;
    }
}
