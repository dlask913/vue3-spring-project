package com.limnj.noticeboardadmin.auth;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QrVerificationService {

    private static final String ISSUER = "limnj";

    // 비밀키 생성
    public GoogleAuthenticatorKey createSecretKey() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        return gAuth.createCredentials();
    }

    // otpauth URI 생성
    public String getOtpAuthURL(String userEmail, String secretKey) {
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                ISSUER, userEmail, secretKey, ISSUER);
    }

    // QR 이미지 Base64 인코딩으로 반환
    public String generateQrCode(String otpAuthUrl) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthUrl, BarcodeFormat.QR_CODE, 250, 250);

        ByteArrayOutputStream pngOutput = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutput);
        return Base64.getEncoder().encodeToString(pngOutput.toByteArray());
    }
}
