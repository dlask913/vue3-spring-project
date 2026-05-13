package com.example.noticeboardservice.controller.member;

import com.example.noticeboardservice.dto.member.LoginRequestDto;
import com.example.noticeboardservice.dto.member.LoginResponseDto;
import com.example.noticeboardservice.dto.member.MemberRequestDto;
import com.example.noticeboardservice.dto.member.MemberResponseDto;
import com.example.noticeboardservice.service.member.MemberService;
import com.example.noticeboardservice.service.util.RecaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberServiceImpl;
    private final RecaptchaService recaptchaService;

    @Value("${httponly-secure-option}")
    private boolean HTTPONLY_SECURE_OPTION;

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
    public ResponseEntity<?> loginMember(@RequestBody LoginRequestDto loginRequestDto) {
        // 리캡차 토큰 검증
        boolean isHuman = recaptchaService.verify(loginRequestDto.getCaptchaToken());
        if (!isHuman) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("로봇 인증에 실패했습니다.");
        }

        LoginResponseDto response = memberServiceImpl.login(loginRequestDto);

        // 1. RefreshToken 을 위한 쿠키 생성
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", response.getRefreshToken())
                .httpOnly(true)
                .secure(HTTPONLY_SECURE_OPTION)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 유효기간 7일
                .sameSite("Lax")
                .build();

        // 2. 응답 DTO 에서 RefreshToken 제거
        response.clearRefreshToken();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(response);
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
