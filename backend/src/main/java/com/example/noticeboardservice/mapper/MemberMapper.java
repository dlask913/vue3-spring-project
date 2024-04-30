package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.MemberDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MemberMapper {
    int insertMember(MemberDto memberDto);
    int updateMember(MemberDto memberDto);
    MemberDto findMember(Long memberId);
    int deleteMember(Long memberId);
    List<MemberDto> findAllMembers();
}
