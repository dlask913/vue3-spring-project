package com.limnj.noticeboardadmin.jwt;

import lombok.Getter;

@Getter
public class RefreshTokenDto {
    String username;
    String refreshToken;
}
