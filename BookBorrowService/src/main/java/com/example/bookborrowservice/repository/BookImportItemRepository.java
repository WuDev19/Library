package com.example.bookborrowservice.repository;

import com.example.bookborrowservice.entity.BookImportItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookImportItemRepository extends JpaRepository<BookImportItem, Long> {
}
