package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.LoginDto;
import com.example.noticeboardservice.dto.LoginResponseDto;
import com.example.noticeboardservice.dto.MemberRequestDto;
import com.example.noticeboardservice.dto.MemberResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface MemberService extends UserDetailsService {
    int registerMember(MemberRequestDto memberRequestDto);
    int updateMember(MemberRequestDto memberRequestDto, MultipartFile memberImg);
    Optional<MemberResponseDto> findMember(Long memberId);
    Optional<MemberResponseDto> findByEmail(String email);
    int deleteMember(Long memberId);
    List<MemberResponseDto> findAllMembers();

    LoginResponseDto login(LoginDto loginDto);
}
