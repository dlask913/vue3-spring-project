package com.limnj.noticeboardadmin.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class LoginRequestDto {
    String username;
    String email;
    String password;
    String authenticationCode;
    String fcmToken;

    @Builder
    public LoginRequestDto(String username, String email, String password, String authenticationCode, String fcmToken) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.authenticationCode = authenticationCode;
        this.fcmToken = fcmToken;
    }
}
