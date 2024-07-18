package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.LoginDto;
import com.example.noticeboardservice.dto.LoginResponseDto;
import com.example.noticeboardservice.dto.MemberDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface MemberService extends UserDetailsService {
    int registerMember(MemberDto memberDto);
    int updateMember(MemberDto memberDto);
    Optional<MemberDto> findMember(Long memberId);
    Optional<MemberDto> findByEmail(String email);
    int deleteMember(Long memberId);
    List<MemberDto> findAllMembers();

    LoginResponseDto login(LoginDto loginDto);
}
