package com.limnj.noticeboardadmin.member;

import org.springframework.stereotype.Service;

@Service
public interface MemberService {
    int saveAdminMember(AdminMemberRequestDto requestDto);
    AdminMemberResponseDto findMemberById(Long memberId);
    boolean loginAdminMember(AdminMemberRequestDto requestDto);
}
