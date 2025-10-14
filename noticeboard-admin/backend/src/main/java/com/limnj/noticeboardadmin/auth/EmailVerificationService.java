package com.limnj.noticeboardadmin.auth;

import org.springframework.stereotype.Service;

@Service
public interface EmailVerificationService {
    boolean sendVerificationCode(String email);
    boolean verifyCode(String email, String code);

}
