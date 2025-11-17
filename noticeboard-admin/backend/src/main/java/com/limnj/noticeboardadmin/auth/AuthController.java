package com.limnj.noticeboardadmin.auth;

import com.google.zxing.WriterException;
import com.limnj.noticeboardadmin.member.LoginRequestDto;
import com.limnj.noticeboardadmin.member.LoginResponseDto;
import com.limnj.noticeboardadmin.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@Slf4j
public class AuthController {
    private final MemberService memberServiceImpl;
    private final EmailVerificationService emailVerificationService;
    private final QrVerificationService qrVerificationService;

    public AuthController(MemberService memberServiceImpl,
                          @Qualifier("concurrentEmailVerificationServiceImpl") EmailVerificationService emailVerificationService, QrVerificationService qrVerificationService) {
        this.memberServiceImpl = memberServiceImpl;
        this.emailVerificationService = emailVerificationService;
        this.qrVerificationService = qrVerificationService;
    }

    @PostMapping("/email-verify")
    @Operation(summary = "2FA 이메일 인증 코드 검증 API")
    public ResponseEntity<?> verifyAuthenticationCode(@RequestBody LoginRequestDto requestDto) {
        log.info("requestDto.toString(): {}", requestDto.toString());
        boolean authResult = emailVerificationService.verifyCode(requestDto.getEmail(), requestDto.getAuthenticationCode());
        if(!authResult){
            return ResponseEntity.badRequest().body("인증 코드 검증에 실패하였습니다.");
        }
        LoginResponseDto loginResponseDto = memberServiceImpl.generateSecondaryAuthToken(requestDto);
        return ResponseEntity.ok().body(loginResponseDto);
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
    public ResponseEntity<?> registerQrAuthenticationCode(@RequestParam String email) throws IOException, WriterException {
        QrResponseDto qrResponseDto = qrVerificationService.generateQrCodeForUser(email);
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
        return ResponseEntity.ok().body(loginResponseDto);
    }
}
