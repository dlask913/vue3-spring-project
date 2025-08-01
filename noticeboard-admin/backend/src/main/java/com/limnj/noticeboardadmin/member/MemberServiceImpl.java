package com.limnj.noticeboardadmin.member;

import com.limnj.noticeboardadmin.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional @RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberMapper memberMapper;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public int saveAdminMember(AdminMemberRequestDto requestDto) {
        return memberMapper.saveAdminMember(requestDto);
    }

    @Override
    public AdminMemberResponseDto findMemberById(Long memberId) {
        return memberMapper.findMemberById(memberId);
    }

    @Override
    public LoginResponseDto loginAdminMember(AdminMemberRequestDto requestDto) {
        AdminMemberResponseDto findMember = memberMapper.findMemberByUsername(requestDto.getUsername()).orElseThrow();
        String token = jwtTokenUtil.generateToken(findMember.getUsername()).getAccessToken();

        return LoginResponseDto.builder()
                .memberId(findMember.getId())
                .token(token)
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AdminMemberResponseDto> findMember = memberMapper.findMemberByUsername(username);
        if (findMember.isEmpty()) {
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
        }
        return User.builder()
                .username(username)
                .password(findMember.get().getPassword())
                .roles("ADMIN")
                .build();
    }
}
