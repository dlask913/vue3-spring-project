package com.limnj.noticeboardadmin.jwt;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenRequestDto {
    String username;
    String refreshToken;

    @Builder
    public RefreshTokenRequestDto(String username, String refreshToken) {
        this.username = username;
        this.refreshToken = refreshToken;
    }
}
