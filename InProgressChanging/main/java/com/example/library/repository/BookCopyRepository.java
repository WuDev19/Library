package com.example.library.repository;

import com.example.library.entity.BookCopy;
import com.example.library.entity.enums.CopyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookCopyRepository extends JpaRepository<BookCopy, Long> {
    List<BookCopy> findByBookBookIdAndStatus(Long bookId, CopyStatus status);
    Optional<BookCopy> findByAssetCode(String assetCode);
    boolean existsByAssetCode(String assetCode);
}
