package com.limnj.noticeboardadmin.member;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LoginRequestDto {
    String username;
    String email;
    String password;
    String authenticationCode;
}
