package com.limnj.noticeboardadmin.fcm;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class FcmTokenRequestDto {
    Long userId;
    String token;
    String deviceType;

    @Builder
    public FcmTokenRequestDto(Long userId, String token, String deviceType) {
        this.userId = userId;
        this.token = token;
        this.deviceType = deviceType;
    }
}
