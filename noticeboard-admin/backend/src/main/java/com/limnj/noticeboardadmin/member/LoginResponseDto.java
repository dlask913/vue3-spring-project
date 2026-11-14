package com.limnj.noticeboardadmin.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class LoginResponseDto {
    Long memberId;
    String username;
    String email;
    String accessToken;
    String refreshToken;
    String role;
    String mfaToken;

    @Builder
    public LoginResponseDto(Long memberId, String username, String email, String accessToken, String refreshToken, String role, String mfaToken) {
        this.memberId = memberId;
        this.username = username;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.role = role;
        this.mfaToken = mfaToken;
    }

    public void clearRefreshToken() {
        this.refreshToken = null;
    }
}
