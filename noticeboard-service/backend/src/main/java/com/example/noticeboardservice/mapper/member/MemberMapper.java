package com.example.noticeboardservice.mapper.member;

import com.example.noticeboardservice.dto.member.MemberRequestDto;
import com.example.noticeboardservice.dto.member.MemberResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {
    int insertMember(MemberRequestDto memberRequestDto);
    int updateMember(MemberRequestDto memberRequestDto);
    MemberResponseDto findMember(Long memberId);
    int deleteMember(Long memberId);
    List<MemberResponseDto> findAllMembers();
    MemberResponseDto findMemberByEmail(String email);
    void deleteAll();
}
