package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MemberDto;

import java.util.List;
import java.util.Optional;

public interface MemberService {
    int registerMember(MemberDto memberDto);
    int updateMember(MemberDto memberDto);
    Optional<MemberDto> findMember(Long memberId);
    int deleteMember(Long memberId);
    List<MemberDto> findAllMembers();
}
