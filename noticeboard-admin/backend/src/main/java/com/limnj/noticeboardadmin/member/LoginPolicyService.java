package com.limnj.noticeboardadmin.member;

import com.limnj.noticeboardadmin.exception.BizException;
import com.limnj.noticeboardadmin.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginPolicyService {
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = BizException.class)
    public void checkPasswordMismatch(String rawPassword, String encodedPassword, String username) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            memberMapper.incrementFailCount(username);
            throw new BizException(ErrorCode.PASSWORD_MISMATCH);
        }
    }
}
