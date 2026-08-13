package com.example.library.repository;

import com.example.library.entity.RefreshToken;
import com.example.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Transactional
    @Modifying
    void deleteByUserRefreshToken(User user);

    Optional<RefreshToken> findByRefreshToken(String refToken);
}
