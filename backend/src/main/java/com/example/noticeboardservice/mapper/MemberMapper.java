package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.MemberRequestDto;
import com.example.noticeboardservice.dto.MemberResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {
    int insertMember(MemberRequestDto memberRequestDto);
    int updateMember(MemberRequestDto memberRequestDto);
    MemberResponseDto findMember(Long memberId);
    int deleteMember(Long memberId);
    List<MemberResponseDto> findAllMembers();
    MemberResponseDto findByEmail(String email);
    void deleteAll();
}
