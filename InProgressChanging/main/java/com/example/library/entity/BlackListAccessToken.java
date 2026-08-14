package com.example.library.entity;

import com.example.library.utils.TimeUtils;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "black_list_access_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(access = AccessLevel.PUBLIC)
public class BlackListAccessToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blacklist_token_id")
    private Long blacklistTokenId;

    @Column(name = "token_id", nullable = false, length = 36, unique = true)
    private String tokenId;

    @Column(name = "expire_date", nullable = false)
    private OffsetDateTime expireDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = TimeUtils.now();
    }
}
