package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.dto.LoginDto;
import com.example.noticeboardservice.dto.LoginResponseDto;
import com.example.noticeboardservice.dto.MemberDto;
import com.example.noticeboardservice.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberServiceImpl;

    @PostMapping("/member")
    @Operation(summary = "회원 가입 API")
    public ResponseEntity<String> registerMember(@RequestBody MemberDto memberDto){
        int result = memberServiceImpl.registerMember(memberDto);
        if (result <= 0){
            return ResponseEntity.badRequest().body("회원가입에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("회원 가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 API")
    public ResponseEntity<LoginResponseDto> loginMember(@RequestBody LoginDto loginDto) {
        LoginResponseDto response = memberServiceImpl.login(loginDto);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "회원 정보 단일 조회 API")
    public ResponseEntity<MemberDto> findMember(@PathVariable("memberId") Long memberId) {
        MemberDto findMember = memberServiceImpl.findMember(memberId).orElseThrow();
        return ResponseEntity.ok().body(findMember);
    }

    @PutMapping("/member")
    @Operation(summary = "회원 정보 수정 API")
    public ResponseEntity<String> updateMember(@RequestBody MemberDto memberDto) {
        int result = memberServiceImpl.updateMember(memberDto);
        if (result <= 0){
            return ResponseEntity.badRequest().body("수정 실패하였습니다.");
        }
        return ResponseEntity.ok().body("수정 완료되었습니다.");
    }

    @DeleteMapping("/member/{memberId}")
    @Operation(summary = "회원 탈퇴 API")
    public ResponseEntity<String> deleteMember(@PathVariable("memberId") Long memberId) {
        int result = memberServiceImpl.deleteMember(memberId);
        if (result <= 0){
            return ResponseEntity.badRequest().body("삭제 실패하였습니다.");
        }
        return ResponseEntity.ok().body("삭제 완료되었습니다.");
    }

}
