package com.limnj.noticeboardadmin.member;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public interface MemberService extends UserDetailsService {
    int saveAdminMember(AdminMemberRequestDto requestDto);
    AdminMemberResponseDto findMemberById(Long memberId);
    LoginResponseDto loginAdminMember(LoginRequestDto requestDto);
    void updateSecretKeyByEmail(String email, String secretKey);
    String findSecretKeyByEmail(String email);
}
