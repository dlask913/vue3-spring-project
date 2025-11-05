package com.limnj.noticeboardadmin.auth;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.limnj.noticeboardadmin.member.MemberService;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class QrVerificationService {

    private static final String ISSUER = "limnj";
    private final MemberService memberServiceImpl;
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public QrResponseDto generateQrCodeForUser(String userEmail) throws IOException, WriterException {
        GoogleAuthenticatorKey secretKey = createSecretKey();
        memberServiceImpl.updateSecretKeyByEmail(userEmail, secretKey.getKey()); // secretKey DB에 저장

        String otpAuthUrl = getOtpAuthURL(userEmail, secretKey.getKey());
        String qrCodeBase64 = generateQrCode(otpAuthUrl);

        return QrResponseDto.builder()
                .qrCodeBase64("data:image/png;base64," + qrCodeBase64)
                .build();
    }

    // 비밀키 생성
    private GoogleAuthenticatorKey createSecretKey() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        return gAuth.createCredentials();
    }

    // otpauth URI 생성
    private String getOtpAuthURL(String userEmail, String secretKey) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                ISSUER, userEmail, secretKey, ISSUER);
    }

    // QR 이미지 Base64 인코딩으로 반환
    private String generateQrCode(String otpAuthUrl) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthUrl, BarcodeFormat.QR_CODE, 250, 250);

        ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutput);
        return Base64.getEncoder().encodeToString(pngOutput.toByteArray());
    }

    /**
     * 사용자가 입력한 OTP 코드가 유효한지 검증
     * @param userCode   사용자가 앱에서 입력한 6자리 코드
     * @return true = 일치, false = 불일치
     */
    public boolean verifyCode(String email, int userCode) {
        String secretKey = memberServiceImpl.findSecretKeyByEmail(email);
        return gAuth.authorize(secretKey, userCode);
    }
}
