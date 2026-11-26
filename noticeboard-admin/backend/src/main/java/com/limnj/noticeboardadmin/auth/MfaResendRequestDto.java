package com.limnj.noticeboardadmin.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class MfaResendRequestDto {
    private String mfaToken;

    @Builder
    public MfaResendRequestDto(String mfaToken) {
        this.mfaToken = mfaToken;
    }
}
