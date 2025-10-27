package com.limnj.noticeboardadmin.auth;

import com.limnj.noticeboardadmin.member.MemberMapper;
import com.limnj.noticeboardadmin.util.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisEmailVerificationServiceImpl implements EmailVerificationService {
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private final MemberMapper memberMapper;

    @Value("${AUTH_EMAIL_SUBJECT}")
    private String AUTH_EMAIL_SUBJECT;
    @Value("${AUTH_EMAIL_TEMPLATE}")
    private String AUTH_EMAIL_TEMPLATE;

    private static final String PREFIX = "email:verify:";
    private static final long EXPIRE_TIME = 3 * 60; // 3분

    @Override
    public boolean sendVerificationCode(String email) {
        // 사용자 존재 확인
        if (!memberMapper.existsByEmail(email)) {
            return false;
        }

        // Redis에 저장 (3분 TTL)
        String key = PREFIX + email;
        String code = String.format("%06d", new Random().nextInt(999999));
        redisTemplate.opsForValue().set(key, code, EXPIRE_TIME, TimeUnit.SECONDS);

        // 이메일 전송
        String htmlTemplate = loadHtmlTemplate(AUTH_EMAIL_TEMPLATE);
        String body = htmlTemplate.replace("${code}", code);
        emailService.sendVerificationCode(email, AUTH_EMAIL_SUBJECT, body);
        return true;
    }

    @Override
    public boolean verifyCode(String email, String inputCode) {
        String key = PREFIX + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode != null && storedCode.equals(inputCode)) {
            redisTemplate.delete(key); // 사용 후 삭제
            return true;
        }
        return false;
    }
}
