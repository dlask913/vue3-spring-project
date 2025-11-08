package com.limnj.noticeboardadmin.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter @NoArgsConstructor
public class LoginRequestDto {
    String username;
    String email;
    String password;
    String authenticationCode;

    @Builder
    public LoginRequestDto(String username, String email, String password, String authenticationCode) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.authenticationCode = authenticationCode;
    }
}
