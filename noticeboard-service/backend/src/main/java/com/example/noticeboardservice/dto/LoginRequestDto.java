package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginRequestDto {
    String email;
    String password;

    @Builder
    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
