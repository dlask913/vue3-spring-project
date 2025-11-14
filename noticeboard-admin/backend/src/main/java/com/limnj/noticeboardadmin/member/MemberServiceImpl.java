package com.limnj.noticeboardadmin.member;

import com.limnj.noticeboardadmin.exception.MemberDuplicateException;
import com.limnj.noticeboardadmin.exception.PasswordMismatchException;
import com.limnj.noticeboardadmin.jwt.JwtTokenUtil;
import com.limnj.noticeboardadmin.jwt.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional @RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberMapper memberMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenService refreshTokenServiceImpl;
    private final PasswordEncoder passwordEncoder;

    @Override
    public int saveAdminMember(AdminMemberRequestDto requestDto) {
        // password 단방향 암호화 적용
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        requestDto.saveEncodedPassword(encodedPassword);

        if (memberMapper.findMemberByUsername(requestDto.getUsername()).isPresent()) {
            throw new MemberDuplicateException();
        }
        return memberMapper.saveAdminMember(requestDto);
    }

    @Override
    public AdminMemberResponseDto findMemberById(Long memberId) {
        return memberMapper.findMemberById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));
    }

    @Override
    public LoginResponseDto loginWithCredentials(LoginRequestDto requestDto) {
        AdminMemberResponseDto findMember = memberMapper.findMemberByUsername(requestDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        boolean authResult = passwordEncoder.matches(requestDto.getPassword(), findMember.getPassword());
        if(!authResult){
            throw new PasswordMismatchException();
        }

        return LoginResponseDto.builder()
                .memberId(findMember.getId())
                .username(findMember.getUsername())
                .email(findMember.getEmail())
                .build();
    }

    @Override
    public LoginResponseDto generateSecondaryAuthToken(LoginRequestDto requestDto) {
        AdminMemberResponseDto findMember = memberMapper.findMemberByUsername(requestDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        String accessToken = jwtTokenUtil.generateToken(findMember.getUsername());
        String refreshToken = refreshTokenServiceImpl.generateRefreshToken(findMember.getUsername());

        return LoginResponseDto.builder()
                .memberId(findMember.getId())
                .username(findMember.getUsername())
                .email(findMember.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public void updateSecretKeyByEmail(String email, String secretKey) {
        // 사용자 존재 확인
        if (!memberMapper.existsByEmail(email)) {
            throw new UsernameNotFoundException("존재하지 않는 사용자입니다.");
        }
        memberMapper.updateSecretKeyByEmail(email, secretKey);
    }

    @Override
    public String findSecretKeyByEmail(String email) {
        return memberMapper.findSecretKeyByEmail(email);
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
