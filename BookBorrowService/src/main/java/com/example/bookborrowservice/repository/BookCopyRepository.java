package com.example.bookborrowservice.repository;

import com.example.bookborrowservice.entity.BookCopy;
import com.example.bookborrowservice.entity.enums.CopyStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {

    @Query("SELECT bc FROM BookCopy bc JOIN FETCH bc.book b WHERE b.bookId = :bookId AND bc.status = :status")
    List<BookCopy> findByBookBookIdAndStatus(@Param("bookId") Long bookId, @Param("status") CopyStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT bc FROM BookCopy bc JOIN FETCH bc.book b WHERE b.bookId = :bookId AND bc.status = :status ORDER BY bc.bookCopyId ASC")
    List<BookCopy> findByBookBookIdAndStatusForUpdate(@Param("bookId") Long bookId, @Param("status") CopyStatus status);
}
