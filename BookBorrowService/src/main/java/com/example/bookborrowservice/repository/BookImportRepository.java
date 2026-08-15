package com.example.bookborrowservice.repository;

import com.example.bookborrowservice.entity.BookImport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookImportRepository extends JpaRepository<BookImport, Long> {
}
