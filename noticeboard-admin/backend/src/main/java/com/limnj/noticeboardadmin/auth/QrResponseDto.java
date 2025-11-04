package com.limnj.noticeboardadmin.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class QrResponseDto {
    String qrCodeBase64;

    @Builder
    public QrResponseDto(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }
}
