package com.limnj.noticeboardadmin.auth;

import com.limnj.noticeboardadmin.member.MemberMapper;
import com.limnj.noticeboardadmin.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ConcurrentEmailVerificationServiceImpl implements EmailVerificationService{

    private final EmailService emailService;
    private final MemberMapper memberMapper;

    @Value("AUTH_EMAIL_SUBJECT")
    private final String AUTH_EMAIL_SUBJECT;
    @Value("AUTH_EMAIL_TEMPLATE")
    private final String AUTH_EMAIL_TEMPLATE;

    private final Map<String, String> codeStore = new ConcurrentHashMap<>(); // 임시 저장소
    private final Map<String, Long> codeExpiry = new ConcurrentHashMap<>();

    /** 1. 이메일로 인증 코드 발송 "**/
    @Override
    public boolean sendVerificationCode(String email) {
        // 사용자 존재 확인
        if (!memberMapper.existsByEmail(email)) {
            return false;
        }

        String code = String.format("%06d", new Random().nextInt(999999));
        String htmlTemplate = loadHtmlTemplate(AUTH_EMAIL_TEMPLATE);
        String body = htmlTemplate.replace("${code}", code);

        emailService.sendVerificationCode(email, AUTH_EMAIL_SUBJECT, body);

        codeStore.put(email, code);
        codeExpiry.put(email, System.currentTimeMillis() + (5 * 60 * 1000)); // 5분 유효
        return true;
    }

    /** 2. 코드 검증 **/
    @Override
    public boolean verifyCode(String email, String code) {
        if(email == null) {
            return false;
        }

        String stored = codeStore.get(email);
        Long expiry = codeExpiry.get(email);

        if (stored == null || expiry == null) return false;
        if (System.currentTimeMillis() > expiry) return false;

        boolean valid = stored.equals(code);
        if (valid) {
            codeStore.remove(email);
            codeExpiry.remove(email);
        }
        return valid;
    }
}
