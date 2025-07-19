package com.limnj.noticeboardadmin.member;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {
    int saveAdminMember(AdminMemberRequestDto requestDto);
}
