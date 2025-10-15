package com.limnj.noticeboardadmin.auth;


public interface EmailVerificationService {
    boolean sendVerificationCode(String email);
    boolean verifyCode(String email, String code);
}
