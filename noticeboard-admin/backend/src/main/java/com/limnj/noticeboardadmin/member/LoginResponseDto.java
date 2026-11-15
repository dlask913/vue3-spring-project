package com.limnj.noticeboardadmin.member;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class LoginResponseDto {
    Long memberId;
    String username;
    String email;
    String accessToken;
    // Refresh Token은 HttpOnly 쿠키로만 전달하고 JSON 응답에서는 제외
    @JsonIgnore
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
