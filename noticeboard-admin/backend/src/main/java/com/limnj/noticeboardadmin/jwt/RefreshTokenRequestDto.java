package com.limnj.noticeboardadmin.jwt;

import lombok.Getter;

@Getter
public class RefreshTokenRequestDto {
    String username;
    String refreshToken;
}
