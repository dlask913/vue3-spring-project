package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MemberDto;
import com.example.noticeboardservice.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberMapper memberMapper;
    @Override
    public int registerMember(MemberDto memberDto) {
        return memberMapper.insertMember(memberDto);
    }
    @Override
    public int updateMember(MemberDto memberDto) {
        return memberMapper.updateMember(memberDto);
    }

    @Override
    public Optional<MemberDto> findMember(Long memberId) {
        return Optional.ofNullable(memberMapper.findMember(memberId));
    }

    @Override
    public int deleteMember(Long memberId) {
        return memberMapper.deleteMember(memberId);
    }

    @Override
    public List<MemberDto> findAllMembers() {
        return memberMapper.findAllMembers();
    }
}
