package com.example.bookborrowservice.service.impl;

import com.example.bookborrowservice.dto.request.BookImportRequest;
import com.example.bookborrowservice.dto.request.BookRequest;
import com.example.bookborrowservice.dto.response.BookResponse;
import com.example.bookborrowservice.entity.*;
import com.example.bookborrowservice.entity.enums.CopyStatus;
import com.example.bookborrowservice.exception.BusinessException;
import com.example.bookborrowservice.exception.ErrorResponse;
import com.example.bookborrowservice.mapper.BookMapper;
import com.example.bookborrowservice.repository.*;
import com.example.bookborrowservice.service.base.IBookService;
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
    private final BookMapper bookMapper;

    @Override
    @Transactional
    public void createBook(BookRequest request) {
        if (bookRepository.existsByCode(request.code())) {
            throw new BusinessException(ErrorResponse.DATA_INTEGRITY);
        }
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));

        Book book = bookMapper.mapToEntity(request, category);

        int quantity = (request.initialQuantity() != null && request.initialQuantity() > 0)
                ? request.initialQuantity()
                : 1;

        book.setTotalQuantity(quantity);
        book.setAvailableQuantity(quantity);
        book = bookRepository.save(book);

        for (int i = 1; i <= quantity; i++) {
            String assetCode = book.getCode() + "-" + String.format("%03d", i);
            BookCopy bookCopy = BookCopy.builder()
                    .book(book)
                    .assetCode(assetCode)
                    .status(CopyStatus.AVAILABLE)
                    .note("Bản sao khởi tạo ban đầu khi tạo sách mới")
                    .build();
            bookCopyRepository.save(bookCopy);
        }
    }

    @Override
    @Transactional
    public void updateBook(Long id, BookRequest request) {
        Book book = bookRepository.findByIdWithCategory(id)
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

        bookRepository.save(book);
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
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findByIdWithCategory(id)
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));
        return bookMapper.mapToResponse(book);
    }

    @Override
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAllWithCategory().stream()
                .map(bookMapper::mapToResponse)
                .toList();
    }

    @Override
    public List<BookResponse> searchBooks(String title, String code) {
        String searchTitle = (title != null && !title.trim().isEmpty()) ? title.trim() : null;
        String searchCode = (code != null && !code.trim().isEmpty()) ? code.trim() : null;

        return bookRepository.searchBooks(searchTitle, searchCode).stream()
                .map(bookMapper::mapToResponse)
                .toList();
    }

    @Override
    public List<BookResponse> getBooksByCategoryCode(String categoryCode) {
        if (categoryCode == null || categoryCode.trim().isEmpty()) {
            return List.of();
        }
        return bookRepository.findByCategoryCodeWithCategory(categoryCode.trim()).stream()
                .map(bookMapper::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void importBooks(BookImportRequest request, Long librarianId) {
        Book book = bookRepository.findByIdWithCategory(request.bookId())
                .orElseThrow(() -> new BusinessException(ErrorResponse.RESOURCE_NOT_FOUND));

        String importCode = "IMP-" + System.currentTimeMillis();
        BookImport bookImport = BookImport.builder()
                .importCode(importCode)
                .book(book)
                .quantity(request.quantity())
                .importedBy(librarianId)
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

        book.setTotalQuantity(book.getTotalQuantity() + request.quantity());
        book.setAvailableQuantity(book.getAvailableQuantity() + request.quantity());
        bookRepository.save(book);
    }
}
