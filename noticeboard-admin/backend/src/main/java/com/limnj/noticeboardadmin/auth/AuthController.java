package com.limnj.noticeboardadmin.auth;

import com.google.zxing.WriterException;
import com.limnj.noticeboardadmin.jwt.RefreshTokenServiceImpl;
import com.limnj.noticeboardadmin.member.LoginRequestDto;
import com.limnj.noticeboardadmin.member.LoginResponseDto;
import com.limnj.noticeboardadmin.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.security.Principal;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class AuthController {
    private final MemberService memberServiceImpl;
    private final EmailVerificationService emailVerificationService;
    private final QrVerificationService qrVerificationService;
    @Value("${httponly-secure-option}")
    private boolean secureCookie;

    public AuthController(MemberService memberServiceImpl,
                          @Qualifier("concurrentEmailVerificationServiceImpl") EmailVerificationService emailVerificationService, QrVerificationService qrVerificationService) {
        this.memberServiceImpl = memberServiceImpl;
        this.emailVerificationService = emailVerificationService;
        this.qrVerificationService = qrVerificationService;
    }

    @PostMapping("/email-verify")
    @Operation(summary = "2FA 이메일 인증 코드 검증 API")
    public ResponseEntity<?> verifyAuthenticationCode(@RequestBody LoginRequestDto requestDto) {
        boolean authResult = emailVerificationService.verifyCode(requestDto.getEmail(), requestDto.getAuthenticationCode());
        if(!authResult){
            return ResponseEntity.badRequest().body("인증 코드 검증에 실패하였습니다.");
        }
        LoginResponseDto loginResponseDto = memberServiceImpl.generateSecondaryAuthToken(requestDto);
        return secondaryAuthenticationResponse(loginResponseDto);
    }

    @PostMapping("/resend-code")
    @Operation(summary = "2FA 이메일 인증 코드 발송 API")
    public ResponseEntity<?> resendAuthenticationCode(@RequestBody LoginRequestDto requestDto) {
        boolean authResult = emailVerificationService.sendVerificationCode(requestDto.getEmail());
        if(!authResult){
            return ResponseEntity.badRequest().body("코드 재발송에 실패하였습니다.");
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/qr-register")
    @Operation(summary = "2FA QR 인증 코드 생성 API")
    public ResponseEntity<?> registerQrAuthenticationCode(Principal principal) throws IOException, WriterException {
        QrResponseDto qrResponseDto = qrVerificationService.generateQrCodeForUser(principal.getName());
        return ResponseEntity.ok().body(qrResponseDto);
    }

    @PostMapping("/qr-verify")
    @Operation(summary = "2FA QR 인증 코드 검증 API")
    public ResponseEntity<?> verifyQrAuthenticationCode(@RequestBody LoginRequestDto requestDto) {
        boolean authResult = qrVerificationService.verifyCode(
                requestDto.getEmail(), Integer.parseInt(requestDto.getAuthenticationCode()));
        if(!authResult){
            return ResponseEntity.badRequest().body("인증 코드 검증에 실패하였습니다.");
        }
        LoginResponseDto loginResponseDto = memberServiceImpl.generateSecondaryAuthToken(requestDto);
        return secondaryAuthenticationResponse(loginResponseDto);
    }

    private ResponseEntity<LoginResponseDto> secondaryAuthenticationResponse(LoginResponseDto loginResponseDto) {
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", loginResponseDto.getRefreshToken())
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .maxAge(RefreshTokenServiceImpl.REFRESH_TOKEN_VALIDITY / 1000)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(loginResponseDto);
    }
}
