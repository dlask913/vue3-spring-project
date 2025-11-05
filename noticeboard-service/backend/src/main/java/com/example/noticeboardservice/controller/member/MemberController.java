package com.example.noticeboardservice.controller.member;

import com.example.noticeboardservice.dto.member.LoginRequestDto;
import com.example.noticeboardservice.dto.member.LoginResponseDto;
import com.example.noticeboardservice.dto.member.MemberRequestDto;
import com.example.noticeboardservice.dto.member.MemberResponseDto;
import com.example.noticeboardservice.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberServiceImpl;

    @PostMapping("/member")
    @Operation(summary = "회원 가입 API")
    public ResponseEntity<String> registerMember(@RequestBody MemberRequestDto memberRequestDto){
        int result = memberServiceImpl.registerMember(memberRequestDto);
        if (result <= 0){
            return ResponseEntity.badRequest().body("회원가입에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("회원 가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    public ResponseEntity<LoginResponseDto> loginMember(@RequestBody LoginRequestDto loginRequestDto) {
        LoginResponseDto response = memberServiceImpl.login(loginRequestDto);
        /** todo
         * HTTPOnly 로 설정하여 토큰값을 헤더에 전달하게 되면 JS 에서 접근이 불가
         * 추후 배포 시 SSL 인증 적용하여 통신 방법 변경
         */
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/member/{memberId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "회원 정보 단일 조회 API")
    public ResponseEntity<MemberResponseDto> findMember(@PathVariable("memberId") Long memberId) {
        MemberResponseDto findMember = memberServiceImpl.findMember(memberId);
        return ResponseEntity.ok().body(findMember);
    }

    @PatchMapping("/member/{memberId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "회원 정보 수정 API")
    public ResponseEntity<String> updateMember( @PathVariable("memberId") Long memberId,
                                                @RequestPart("memberDto") MemberRequestDto memberRequestDto,
                                                @RequestPart(value = "memberImg", required = false) MultipartFile memberImg) {
        int result = memberServiceImpl.updateMember(memberRequestDto, memberImg);
        if (result <= 0){
            return ResponseEntity.badRequest().body("수정 실패하였습니다.");
        }
        return ResponseEntity.ok().body("수정 완료되었습니다.");
    }

    @DeleteMapping("/member/{memberId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "회원 탈퇴 API")
    public ResponseEntity<String> deleteMember(@PathVariable("memberId") Long memberId) {
        int result = memberServiceImpl.deleteMember(memberId);
        if (result <= 0){
            return ResponseEntity.badRequest().body("삭제 실패하였습니다.");
        }
        return ResponseEntity.ok().body("삭제 완료되었습니다.");
    }

}
