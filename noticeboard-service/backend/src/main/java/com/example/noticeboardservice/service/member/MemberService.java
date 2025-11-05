package com.example.noticeboardservice.service.member;

import com.example.noticeboardservice.dto.member.LoginRequestDto;
import com.example.noticeboardservice.dto.member.LoginResponseDto;
import com.example.noticeboardservice.dto.member.MemberRequestDto;
import com.example.noticeboardservice.dto.member.MemberResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface MemberService extends UserDetailsService {
    int registerMember(MemberRequestDto memberRequestDto);
    int updateMember(MemberRequestDto memberRequestDto, MultipartFile memberImg);
    MemberResponseDto findMember(Long memberId);
    Optional<MemberResponseDto> findMemberByEmail(String email);
    int deleteMember(Long memberId);
    List<MemberResponseDto> findAllMembers();

    LoginResponseDto login(LoginRequestDto loginRequestDto);
}
