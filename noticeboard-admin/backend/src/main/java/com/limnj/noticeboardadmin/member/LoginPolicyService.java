package com.limnj.noticeboardadmin.member;

import com.limnj.noticeboardadmin.exception.BizException;
import com.limnj.noticeboardadmin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service @Slf4j
@RequiredArgsConstructor
public class LoginPolicyService {
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = BizException.class)
    public void validatePassword(String rawPassword, String encodedPassword, Long memberId) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            log.info("잘못된 비밀번호입니다.");
            memberMapper.incrementFailCount(memberId);
            throw new BizException(ErrorCode.PASSWORD_MISMATCH);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = BizException.class)
    public void validateAccountStatus(LocalDateTime lockUntil) {
        if (lockUntil != null && lockUntil.isAfter(LocalDateTime.now())) {
            log.info("일시적으로 잠금된 계정입니다.");
            throw new BizException(ErrorCode.ACCOUNT_LOCKED); // 5분 잠금 상태
        }
    }
}
