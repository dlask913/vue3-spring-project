package com.limnj.noticeboardadmin.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional @RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberMapper memberMapper;

    @Override
    public int saveAdminMember(AdminMemberRequestDto requestDto) {
        return memberMapper.saveAdminMember(requestDto);
    }

    @Override
    public AdminMemberResponseDto findMemberById(Long memberId) {
        return memberMapper.findMemberById(memberId);
    }
}
