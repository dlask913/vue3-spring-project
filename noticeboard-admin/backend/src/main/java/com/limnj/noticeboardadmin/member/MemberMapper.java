package com.limnj.noticeboardadmin.member;

import org.apache.ibatis.annotations.Mapper;

import java.util.Optional;

@Mapper
public interface MemberMapper {
    int saveAdminMember(AdminMemberRequestDto requestDto);
    Optional<AdminMemberResponseDto> findMemberById(Long memberId);
    Optional<AdminMemberResponseDto> findMemberByUsername(String username);
    boolean existsByEmail(String email);
}
