package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByCode(String code);
    boolean existsByCode(String code);

    @Query("""
        SELECT DISTINCT b
        FROM Book b
        LEFT JOIN BookCopy bc ON bc.book.bookId = b.bookId
        LEFT JOIN BorrowRecord br ON br.bookCopy.bookCopyId = bc.bookCopyId
        LEFT JOIN br.borrower borrower
        WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%')))
          AND (:code IS NULL OR LOWER(b.code) LIKE LOWER(CONCAT('%', :code, '%')))
          AND (:borrower IS NULL OR (
                LOWER(borrower.fullName) LIKE LOWER(CONCAT('%', :borrower, '%')) 
                OR LOWER(borrower.username) LIKE LOWER(CONCAT('%', :borrower, '%'))
              ))
    """)
    List<Book> searchBooks(
        @Param("title") String title, 
        @Param("code") String code, 
        @Param("borrower") String borrower
    );
}
