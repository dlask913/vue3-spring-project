package com.limnj.noticeboardadmin.fcm;

import lombok.Getter;

@Getter
public class FcmTokenResponseDto {
    Long userId;
    String token;
}
