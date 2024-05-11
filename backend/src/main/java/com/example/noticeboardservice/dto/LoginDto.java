package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LoginDto {
    String email;
    String password;

    @Builder
    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
