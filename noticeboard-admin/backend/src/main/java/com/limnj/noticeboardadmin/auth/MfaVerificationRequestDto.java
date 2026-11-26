package com.limnj.noticeboardadmin.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class MfaVerificationRequestDto {
    private String mfaToken;
    @NotBlank(message = "인증 코드가 필요합니다.")
    @Pattern(regexp = "[0-9]{6}", message = "인증 코드는 숫자 6자리여야 합니다.")
    private String authenticationCode;
    private String fcmToken;

    @Builder
    public MfaVerificationRequestDto(String mfaToken, String authenticationCode, String fcmToken) {
        this.mfaToken = mfaToken;
        this.authenticationCode = authenticationCode;
        this.fcmToken = fcmToken;
    }
}
