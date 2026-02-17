package com.limnj.noticeboardadmin.member;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface MemberMapper {
    int saveAdminMember(AdminMemberRequestDto requestDto);
    Optional<AdminMemberResponseDto> findMemberById(Long memberId);
    Optional<AdminMemberResponseDto> findMemberByUsername(String username);
    boolean existsByEmail(String email);
    void updateSecretKeyByEmail(@Param("email") String email, @Param("secretKey") String secretKey);
    Optional<String> findSecretKeyByEmail(String email);
    void incrementFailCount(String username);
}
