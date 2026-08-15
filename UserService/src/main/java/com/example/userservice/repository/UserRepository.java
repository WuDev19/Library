package com.example.userservice.repository;

import com.example.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Tìm user đang active (chưa bị xóa mềm) theo ID
     */
    @Query("SELECT u FROM User u WHERE u.userId = :userId AND u.deletedAt IS NULL")
    Optional<User> findActiveById(@Param("userId") Long userId);

    /**
     * Tìm kiếm user theo từ khóa (tên, email, phone) - chỉ user chưa bị xóa mềm
     * LIBRARIAN mới được gọi method này
     */
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL AND (" +
           "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "u.phone LIKE CONCAT('%', :keyword, '%'))")
    List<User> searchByKeyword(@Param("keyword") String keyword);

    /**
     * Lấy tất cả user đang active
     */
    @Query("SELECT u FROM User u WHERE u.deletedAt IS NULL ORDER BY u.createdAt DESC")
    List<User> findAllActive();
}
