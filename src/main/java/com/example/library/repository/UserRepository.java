package com.example.library.repository;

import com.example.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsernameAndIsActiveIsTrue(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndIsActiveIsTrue(String email);
}
