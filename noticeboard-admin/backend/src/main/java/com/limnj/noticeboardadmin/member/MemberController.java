package com.limnj.noticeboardadmin.member;

import com.limnj.noticeboardadmin.auth.EmailVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MemberController {
    private final MemberService memberServiceImpl;
    private final EmailVerificationService emailVerificationService;

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
    @Operation(summary = "로그인 API")
    public ResponseEntity<?> loginMember(@RequestBody LoginRequestDto requestDto){
        LoginResponseDto loginResponseDto = memberServiceImpl.loginAdminMember(requestDto);
        boolean authResult = emailVerificationService.sendVerificationCode(loginResponseDto.getEmail());// 인증 코드 전송
        if(!authResult){
            return ResponseEntity.badRequest().body("인증 코드 전송에 실패하였습니다.");
        }
        return ResponseEntity.ok().body(loginResponseDto);
    }
}
