package com.example.bookborrowservice.repository;

import com.example.bookborrowservice.entity.BorrowRecord;
import com.example.bookborrowservice.entity.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    @Query("SELECT r FROM BorrowRecord r JOIN FETCH r.bookCopy bc JOIN FETCH bc.book b WHERE r.borrowCode = :borrowCode")
    Optional<BorrowRecord> findByBorrowCodeWithDetails(@Param("borrowCode") String borrowCode);

    @Query("SELECT r FROM BorrowRecord r JOIN FETCH r.bookCopy bc JOIN FETCH bc.book b WHERE r.borrowRecordId = :id")
    Optional<BorrowRecord> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT r FROM BorrowRecord r JOIN FETCH r.bookCopy bc JOIN FETCH bc.book b WHERE r.borrowerId = :userId ORDER BY r.createdAt DESC")
    List<BorrowRecord> findByBorrowerUserIdWithDetails(@Param("userId") Long userId);

    @Query("SELECT r FROM BorrowRecord r JOIN FETCH r.bookCopy bc JOIN FETCH bc.book b ORDER BY r.createdAt DESC")
    List<BorrowRecord> findAllWithDetails();

    @Query("SELECT r FROM BorrowRecord r JOIN FETCH r.bookCopy bc JOIN FETCH bc.book b WHERE r.borrowerId IN :userIds AND r.status IN (:statuses)")
    List<BorrowRecord> findActiveBorrowsByUserIdsWithDetails(@Param("userIds") List<Long> userIds, @Param("statuses") List<BorrowStatus> statuses);
}
