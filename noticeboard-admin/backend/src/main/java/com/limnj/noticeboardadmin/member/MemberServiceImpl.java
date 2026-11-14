package com.limnj.noticeboardadmin.member;

import com.limnj.noticeboardadmin.audit.AuditLog;
import com.limnj.noticeboardadmin.auth.MfaTokenService;
import com.limnj.noticeboardadmin.exception.BizException;
import com.limnj.noticeboardadmin.exception.ErrorCode;
import com.limnj.noticeboardadmin.fcm.FcmNotificationService;
import com.limnj.noticeboardadmin.fcm.FcmTokenRequestDto;
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
    private final LoginPolicyService loginPolicyService;
    private final FcmNotificationService fcmNotificationService;
    private final MfaTokenService mfaTokenService;

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

        loginPolicyService.validateAccountStatus(findMember.getLockUntil());
        loginPolicyService.validatePassword(requestDto.getPassword(), findMember.getPassword(), findMember.getId());

        // 로그인 토큰 대신 2차 인증 대기용 토큰을 발급하고 서버에서 확인한 회원 ID와 연결
        String mfaToken = mfaTokenService.issueToken(findMember.getId());
        return LoginResponseDto.builder()
                .memberId(findMember.getId())
                .username(findMember.getUsername())
                .email(findMember.getEmail())
                .role(findMember.getRole())
                .mfaToken(mfaToken)
                .build();
    }

    @Override
    public LoginResponseDto generateSecondaryAuthToken(LoginRequestDto requestDto) {
        AdminMemberResponseDto findMember = memberMapper.findMemberByUsername(requestDto.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자입니다."));

        // FCM 토큰 저장
        FcmTokenRequestDto fcmTokenRequestDto = FcmTokenRequestDto.builder()
                .userId(findMember.getId())
                .token(requestDto.getFcmToken() == null ? "NULL" : requestDto.getFcmToken())
                .build();
        fcmNotificationService.updateFcmTokenForUser(fcmTokenRequestDto);

        memberMapper.resetFailCount(findMember.getId());

        // accessToken 및 refreshToken 생성
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
    public void updateSecretKeyByUsername(String username, String secretKey) {
        AdminMemberResponseDto member = memberMapper.findMemberByUsername(username)
                .orElseThrow(() -> new BizException(ErrorCode.MEMBER_NOT_FOUND));
        memberMapper.updateSecretKeyById(member.getId(), secretKey);
    }

    @Override
    public String findSecretKeyByEmail(String email) {
        return memberMapper.findSecretKeyByEmail(email)
                .orElseThrow(() -> new BizException(ErrorCode.QR_NOT_GENERATE)); // QR 생성하지 않은 회원 예외 처리
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

    @Override
    public void logoutMember(LogoutRequestDto requestDto) {
        /**
         * - fcmToken 의 userId NULL 로 UPDATE (UNBIND)
         * - userId 의 refreshToken 데이터 DELETE
         */
        fcmNotificationService.unbindFcmToken(requestDto.getFcmToken());
        refreshTokenServiceImpl.expireRefreshToken(requestDto.getUsername());
    }
}
