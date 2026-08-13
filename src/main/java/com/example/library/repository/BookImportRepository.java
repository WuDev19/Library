package com.example.library.repository;

import com.example.library.entity.BookImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookImportRepository extends JpaRepository<BookImport, Long> {
    boolean existsByImportCode(String importCode);
}
