package com.example.bookborrowservice.repository;

import com.example.bookborrowservice.entity.Book;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByCode(String code);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.category WHERE b.bookId = :id")
    Optional<Book> findByIdWithCategory(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.category WHERE b.bookId = :id")
    Optional<Book> findByIdWithCategoryForUpdate(@Param("id") Long id);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.category ORDER BY b.createdAt DESC")
    Page<Book> findAllWithCategory(Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN FETCH b.category " +
           "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:code IS NULL OR LOWER(b.code) LIKE LOWER(CONCAT('%', :code, '%')))")
    Page<Book> searchBooks(@Param("title") String title, @Param("code") String code, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN FETCH b.category c " +
           "WHERE LOWER(c.code) = LOWER(:categoryCode)")
    Page<Book> findByCategoryCodeWithCategory(@Param("categoryCode") String categoryCode, Pageable pageable);
}
