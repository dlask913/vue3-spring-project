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

    @Builder
    public LoginResponseDto(Long memberId, String username, String email, String accessToken, String refreshToken) {
        this.memberId = memberId;
        this.username = username;
        this.email = email;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
