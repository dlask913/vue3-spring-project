package com.limnj.noticeboardadmin.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @NoArgsConstructor
public class RefreshToken {
    String username;
    String token;
    LocalDateTime expiryDate;

    @Builder
    public RefreshToken(String username, String token, LocalDateTime expiryDate) {
        this.username = username;
        this.token = token;
        this.expiryDate = expiryDate;
    }
}
