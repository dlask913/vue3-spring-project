package com.limnj.noticeboardadmin.member;

import com.limnj.noticeboardadmin.audit.AuditLog;
import com.limnj.noticeboardadmin.exception.BizException;
import com.limnj.noticeboardadmin.exception.ErrorCode;
import com.limnj.noticeboardadmin.jwt.JwtTokenUtil;
import com.limnj.noticeboardadmin.jwt.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional @RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberMapper memberMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private final RefreshTokenService refreshTokenServiceImpl;
    private final PasswordEncoder passwordEncoder;
    private final LoginPolicyService loginPolicyService;

    @Override
    @AuditLog(eventType = AuditLog.EventType.REGISTER_MEMBER, actionType = AuditLog.ActionType.CREATE)
    public int saveAdminMember(AdminMemberRequestDto requestDto) {
        // password 단방향 암호화 적용
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        requestDto.saveEncodedPassword(encodedPassword);

        if (memberMapper.findMemberByUsername(requestDto.getUsername()).isPresent()) {
            throw new BizException(ErrorCode.MEMBER_DUPLICATE);
        }
        return memberMapper.saveAdminMember(requestDto);
    }

    @Override
    public AdminMemberResponseDto findMemberById(Long memberId) {
        return memberMapper.findMemberById(memberId)
                .orElseThrow(() -> new BizException(ErrorCode.MEMBER_NOT_FOUND));
    }

    @Override
    @AuditLog(eventType = AuditLog.EventType.FIRST_LOGIN, actionType = AuditLog.ActionType.LOGIN)
    public LoginResponseDto loginWithCredentials(LoginRequestDto requestDto) {
        AdminMemberResponseDto findMember = memberMapper.findMemberByUsername(requestDto.getUsername())
                .orElseThrow(() -> new BizException(ErrorCode.MEMBER_NOT_FOUND));

        if (findMember.getLockUntil() != null && findMember.getLockUntil().isAfter(LocalDateTime.now())) {
            throw new BizException(ErrorCode.ACCOUNT_LOCKED); // 5분 잠금 상태
        }

        loginPolicyService.checkPasswordMismatch(requestDto.getPassword(), findMember.getPassword(), requestDto.getUsername());

        return LoginResponseDto.builder()
                .memberId(findMember.getId())
                .username(findMember.getUsername())
                .email(findMember.getEmail())
                .role(findMember.getRole())
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
                .role(findMember.getRole())
                .build();
    }

    @Override
    @AuditLog(eventType = AuditLog.EventType.QR_SECRET_KEY, actionType = AuditLog.ActionType.UPDATE)
    public void updateSecretKeyByEmail(String email, String secretKey) {
        // 사용자 존재 확인
        if (!memberMapper.existsByEmail(email)) {
            throw new BizException(ErrorCode.EMAIL_NOT_FOUND);
        }
        memberMapper.updateSecretKeyByEmail(email, secretKey);
    }

    @Override
    public String findSecretKeyByEmail(String email) {
        return memberMapper.findSecretKeyByEmail(email)
                .orElseThrow(() -> new BizException(ErrorCode.QR_NOT_GENERATE)); // QR 생성하지 않은 회원 예외 처리
    }

    @Override
    public boolean existsByEmail(String email) {
        return memberMapper.existsByEmail(email);
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
                .authorities(findMember.get().getRole())
                .build();
    }
}
