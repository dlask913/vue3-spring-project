package com.limnj.noticeboardadmin.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.limnj.noticeboardadmin.member.MemberMapper;
import com.limnj.noticeboardadmin.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CaffeineEmailVerificationServiceImpl implements EmailVerificationService{

    private final EmailService emailService;
    private final MemberMapper memberMapper;

    // Caffeine Cache: key=email, value=code
    private final Cache<String, String> codeCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES) // 5분 후 자동 만료
            .maximumSize(1000) // 최대 캐시 항목 수
            .build();

    private static final String AUTH_EMAIL_SUBJECT = "[인증코드 발송]";
    private static final String AUTH_EMAIL_BODY = "인증코드 {%s}를 입력해주세요.";

    /** 1. 이메일로 인증 코드 발송 **/
    @Override
    public boolean sendVerificationCode(String email) {
        if (!memberMapper.existsByEmail(email)) {
            return false;
        }

        String code = String.format("%06d", new Random().nextInt(999999));
        String body = String.format(AUTH_EMAIL_BODY, code);

        emailService.sendVerificationCode(email, AUTH_EMAIL_SUBJECT, body);

        codeCache.put(email, code);
        return true;
    }

    /** 2. 코드 검증 **/
    @Override
    public boolean verifyCode(String email, String code) {
        String stored = codeCache.getIfPresent(email);
        if (stored == null) return false;

        boolean valid = stored.equals(code);
        if (valid) {
            codeCache.invalidate(email); // 사용 후 즉시 제거
        }
        return valid;
    }
}
