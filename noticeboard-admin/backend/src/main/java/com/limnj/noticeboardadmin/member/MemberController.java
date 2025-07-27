package com.limnj.noticeboardadmin.member;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberServiceImpl;

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
    public ResponseEntity<LoginResponseDto> loginMember(@RequestBody AdminMemberRequestDto requestDto){
        LoginResponseDto loginResponseDto = memberServiceImpl.loginAdminMember(requestDto);
        return ResponseEntity.ok().body(loginResponseDto);
    }
}
