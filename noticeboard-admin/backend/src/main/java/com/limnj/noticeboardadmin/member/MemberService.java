package com.limnj.noticeboardadmin.member;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface MemberService extends UserDetailsService {
    /* 회원가입 */
    int saveAdminMember(AdminMemberRequestDto requestDto);
    /* PK 로 회원 조회 */
    AdminMemberResponseDto findMemberById(Long memberId);
    /* 유저 ID & Password 검증 - 1차 로그인 */
    LoginResponseDto loginWithCredentials(LoginRequestDto requestDto);
    /* 2차 인증 성공 후 토큰 발급 - 2차 로그인 */
    LoginResponseDto generateSecondaryAuthToken(LoginRequestDto requestDto);
    /* QR 시크릿 키 저장 */
    void updateSecretKeyByEmail(String email, String secretKey);
    /* QR 시크릿 키 조회 */
    String findSecretKeyByEmail(String email);
    boolean existsByEmail(String email);
}
