package com.limnj.noticeboardadmin.member;

import com.limnj.noticeboardadmin.auth.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MemberController {
    private final MemberService memberServiceImpl;
    private final EmailVerificationService emailVerificationService;
    @Value("${httponly-secure-option}")
    private boolean secureCookie;

    public MemberController(MemberService memberServiceImpl,
                            @Qualifier("concurrentEmailVerificationServiceImpl") EmailVerificationService emailVerificationService) {
        this.memberServiceImpl = memberServiceImpl;
        this.emailVerificationService = emailVerificationService;
    }

    @PostMapping("/member")
    @Operation(summary = "회원 가입 API")
    public ResponseEntity<String> registerMember(@RequestBody AdminMemberRequestDto requestDto){
        int result = memberServiceImpl.saveAdminMember(requestDto);
        if (result <= 0){
            return ResponseEntity.badRequest().body("회원가입에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("회원 가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    @Operation(summary = "1차 로그인 API - ID&Password 검증")
    public ResponseEntity<?> loginMember(@RequestBody LoginRequestDto requestDto){
        LoginResponseDto loginResponseDto = memberServiceImpl.loginWithCredentials(requestDto);
        boolean authResult = emailVerificationService.sendVerificationCode(loginResponseDto.getEmail());// 인증 코드 전송
        if(!authResult){
            return ResponseEntity.badRequest().body("인증 코드 전송에 실패하였습니다.");
        }

        // 1차 인증에서는 mfaToken만 발급하고 로그인 완료용 쿠키는 2차 인증에서 설정
        return ResponseEntity.ok().body(loginResponseDto);
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public ResponseEntity<Void> logoutMember(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @RequestBody(required = false) LogoutRequestDto requestDto) {
        memberServiceImpl.logoutMember(refreshToken, requestDto == null ? null : requestDto.getFcmToken());

        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(secureCookie)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, expiredCookie.toString())
                .build();
    }
}
