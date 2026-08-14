package com.example.authservice.repository;

import com.example.authservice.entity.Account;
import com.example.authservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @Transactional
    @Modifying
    void deleteByAccount(Account account);

    Optional<RefreshToken> findByRefreshToken(String refToken);
}
