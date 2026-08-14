package com.example.library.repository;

import com.example.library.entity.BlackListAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlackListAccessTokenRepository extends JpaRepository<BlackListAccessToken, Long> {
    boolean existsByTokenId(String token);

}
