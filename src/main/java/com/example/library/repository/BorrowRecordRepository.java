package com.example.library.repository;

import com.example.library.entity.BorrowRecord;
import com.example.library.entity.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {
    Optional<BorrowRecord> findByBorrowCode(String borrowCode);
    boolean existsByBorrowCode(String borrowCode);
    List<BorrowRecord> findByBorrowerUserId(Long borrowerId);
    List<BorrowRecord> findByStatus(BorrowStatus status);
    List<BorrowRecord> findByStatusIn(List<BorrowStatus> statuses);

    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWING' AND br.dueDate < :today")
    List<BorrowRecord> findOverdueRecords(@Param("today") LocalDate today);

    @Query("SELECT br FROM BorrowRecord br WHERE br.status = 'BORROWING' AND br.dueDate BETWEEN :startDate AND :endDate")
    List<BorrowRecord> findRecordsDueSoon(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
