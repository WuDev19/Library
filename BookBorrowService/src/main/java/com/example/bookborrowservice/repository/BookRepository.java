package com.example.bookborrowservice.repository;

import com.example.bookborrowservice.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByCode(String code);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.category WHERE b.bookId = :id")
    Optional<Book> findByIdWithCategory(@Param("id") Long id);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.category ORDER BY b.createdAt DESC")
    List<Book> findAllWithCategory();

    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN FETCH b.category " +
           "WHERE (:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:code IS NULL OR LOWER(b.code) LIKE LOWER(CONCAT('%', :code, '%')))")
    List<Book> searchBooks(@Param("title") String title, @Param("code") String code);

    @Query("SELECT DISTINCT b FROM Book b " +
           "LEFT JOIN FETCH b.category c " +
           "WHERE LOWER(c.code) = LOWER(:categoryCode)")
    List<Book> findByCategoryCodeWithCategory(@Param("categoryCode") String categoryCode);
}
