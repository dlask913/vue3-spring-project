package com.limnj.noticeboardadmin.auth;

import com.google.zxing.WriterException;
import com.limnj.noticeboardadmin.jwt.RefreshTokenServiceImpl;
import com.limnj.noticeboardadmin.member.AdminMemberResponseDto;
import com.limnj.noticeboardadmin.exception.BizException;
import com.limnj.noticeboardadmin.exception.ErrorCode;
import jakarta.validation.Valid;
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
    private final MfaTokenService mfaTokenService;
    @Value("${httponly-secure-option}")
    private boolean secureCookie;

    public AuthController(MemberService memberServiceImpl,
                          @Qualifier("concurrentEmailVerificationServiceImpl") EmailVerificationService emailVerificationService,
                          QrVerificationService qrVerificationService, MfaTokenService mfaTokenService) {
        this.memberServiceImpl = memberServiceImpl;
        this.emailVerificationService = emailVerificationService;
        this.qrVerificationService = qrVerificationService;
        this.mfaTokenService = mfaTokenService;
    }

    @PostMapping("/email-verify")
    @Operation(summary = "2FA 이메일 인증 코드 검증 API")
    public ResponseEntity<?> verifyAuthenticationCode(@Valid @RequestBody MfaVerificationRequestDto requestDto) {
        AdminMemberResponseDto member = findPendingMember(requestDto.getMfaToken());
        boolean authResult = emailVerificationService.verifyCode(member.getEmail(), requestDto.getAuthenticationCode());
        if(!authResult){
            return ResponseEntity.badRequest().body("인증 코드 검증에 실패하였습니다.");
        }
        return completeAuthentication(requestDto, member.getId());
    }

    @PostMapping("/resend-code")
    @Operation(summary = "2FA 이메일 인증 코드 발송 API")
    public ResponseEntity<?> resendAuthenticationCode(@RequestBody MfaResendRequestDto requestDto) {
        AdminMemberResponseDto member = findPendingMember(requestDto.getMfaToken());
        boolean authResult = emailVerificationService.sendVerificationCode(member.getEmail());
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
    public ResponseEntity<?> verifyQrAuthenticationCode(@Valid @RequestBody MfaVerificationRequestDto requestDto) {
        AdminMemberResponseDto member = findPendingMember(requestDto.getMfaToken());
        boolean authResult = qrVerificationService.verifyCode(
                member.getEmail(), Integer.parseInt(requestDto.getAuthenticationCode()));
        if(!authResult){
            return ResponseEntity.badRequest().body("인증 코드 검증에 실패하였습니다.");
        }
        return completeAuthentication(requestDto, member.getId());
    }

    private AdminMemberResponseDto findPendingMember(String mfaToken) {
        Long memberId = mfaTokenService.findMemberId(mfaToken)
                .orElseThrow(() -> new BizException(ErrorCode.MFA_TOKEN_INVALID));
        return memberServiceImpl.findMemberById(memberId);
    }

    private ResponseEntity<LoginResponseDto> completeAuthentication(MfaVerificationRequestDto requestDto, Long memberId) {
        // 검증 도중 만료됐거나 다른 요청이 먼저 완료했다면 발급하지 않는다.
        if (!mfaTokenService.consumeToken(requestDto.getMfaToken(), memberId)) {
            throw new BizException(ErrorCode.MFA_TOKEN_INVALID);
        }
        LoginResponseDto loginResponseDto = memberServiceImpl.generateSecondaryAuthToken(memberId, requestDto.getFcmToken());
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
