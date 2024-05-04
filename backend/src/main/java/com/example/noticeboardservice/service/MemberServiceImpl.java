package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.LoginDto;
import com.example.noticeboardservice.dto.MemberDto;
import com.example.noticeboardservice.mapper.MemberMapper;
import com.example.noticeboardservice.utils.JwtTokenUtil;
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
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public String login(LoginDto loginDto) {
        MemberDto memberDto = findByEmail(loginDto.getEmail())
                .orElseThrow(
                        () -> new RuntimeException("회원 정보가 없습니다.")
                );

        if (!(memberDto.getEmail().equals(loginDto.getEmail())
                && memberDto.getPassword().equals(loginDto.getPassword()))) {
            throw new RuntimeException("비밀번호가 맞지 않습니다.");
        }

        return jwtTokenUtil.generateToken(memberDto.getEmail());
    }

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
    public Optional<MemberDto> findByEmail(String email) {
        return Optional.ofNullable(memberMapper.findByEmail(email));
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
