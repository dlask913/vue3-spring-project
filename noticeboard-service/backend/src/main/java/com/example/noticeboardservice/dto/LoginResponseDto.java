package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class LoginResponseDto {
    Long memberId;
    String token;

    @Builder
    public LoginResponseDto(Long memberId, String token) {
        this.memberId = memberId;
        this.token = token;
    }
}
