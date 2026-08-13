package com.example.library.repository;

import com.example.library.entity.BookImportItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookImportItemRepository extends JpaRepository<BookImportItem, Long> {
}
