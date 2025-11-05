package com.example.noticeboardservice.dto.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class LoginResponseDto {
    Long memberId;
    String accessToken;
    String refreshToken;

    @Builder
    public LoginResponseDto(Long memberId, String accessToken, String refreshToken) {
        this.memberId = memberId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
