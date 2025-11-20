package com.limnj.noticeboardadmin.auth;

import com.google.zxing.WriterException;
import com.limnj.noticeboardadmin.member.MemberService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class QrVerificationServiceTest {
    @Autowired
    private QrVerificationService qrService;
    @MockBean
    private MemberService memberService; // Mock 대상

    @Test
    @DisplayName("QR 코드를 생성한다")
    void generateQrCodeForUser_success() throws IOException, WriterException {
        // given
        String userEmail = "test@example.com";
        when(memberService.existsByEmail(userEmail)).thenReturn(true);

        // when
        QrResponseDto response = qrService.generateQrCodeForUser(userEmail);

        // then
        assertNotNull(response);
        assertTrue(response.getQrCodeBase64().startsWith("data:image/png;base64,"));
    }

    @Test
    @DisplayName("유효한 QR 인증번호를 검증한다")
    void verifyCode_success() {
        // given
        String email = "user@test.com";
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        String secret = key.getKey();
        int validCode = gAuth.getTotpPassword(secret); // 현재 시점 유효 코드 생성
        when(memberService.findSecretKeyByEmail(email)).thenReturn(secret);

        // when
        boolean result = qrService.verifyCode(email, validCode);

        // then
        assertTrue(result);
        verify(memberService).findSecretKeyByEmail(email);
    }

    @Test
    @DisplayName("QR 인증번호가 유효하지 않으면 false 를 반환한다")
    void verifyCode_failure() {
        // given
        String email = "user@test.com";
        when(memberService.findSecretKeyByEmail(email)).thenReturn("FAKESECRET123");

        // when
        boolean result = qrService.verifyCode(email, 123456);

        // then
        assertFalse(result);
    }
}