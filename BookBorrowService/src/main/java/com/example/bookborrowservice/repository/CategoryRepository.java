package com.example.bookborrowservice.repository;

import com.example.bookborrowservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByCode(String code);
}
