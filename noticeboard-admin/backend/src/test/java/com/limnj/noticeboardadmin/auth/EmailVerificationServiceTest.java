package com.limnj.noticeboardadmin.auth;

import com.limnj.noticeboardadmin.member.AdminMemberRequestDto;
import com.limnj.noticeboardadmin.member.MemberMapper;
import com.limnj.noticeboardadmin.util.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class EmailVerificationServiceTest {

    private static final Logger log = LoggerFactory.getLogger(EmailVerificationServiceTest.class);
    @Autowired
    @Qualifier("redisEmailVerificationServiceImpl")
    private EmailVerificationService emailVerificationService;

    @Autowired
    private MemberMapper memberMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Mock
    private EmailService emailService; // 실제 이메일 발송은 mock

    @Test
    @DisplayName("ConcurrentHashMap 을 통해 이메일 인증 코드를 저장 및 검증한다.")
    void verifyCodeByConcurrentHashMapTest() throws Exception {
        // given
        String email = "user@example.com";
        saveMember(email);
        emailVerificationService.sendVerificationCode(email);

        // 내부 저장소(codeStore) 접근
        Map<String, String> codeStore = getPrivateField(emailVerificationService, "codeStore");
        String storedCode = codeStore.get(email);

        // when
        boolean result = emailVerificationService.verifyCode(email, storedCode);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Caffeine Cache 를 통해 이메일 인증 코드를 저장 및 검증한다.")
    void verifyCodeByCaffeineCacheTest() throws Exception {
        // given
        String email = "user@example.com";
        saveMember(email);
        emailVerificationService.sendVerificationCode(email);

        Map<String, String> cacheMap = getPrivateCacheMap(emailVerificationService);
        String storedCode = cacheMap.get(email);

        // when
        boolean result = emailVerificationService.verifyCode(email, storedCode);

        // then
        assertThat(result).isTrue();
        assertThat(cacheMap).doesNotContainKey(email);
    }

    @Test
    @DisplayName("Redis 를 통해 이메일 인증 코드를 저장 및 검증한다.")
    void verifyCodeByRedisTest() throws Exception {
        // given
        String email = "user@example.com";
        saveMember(email);
        emailVerificationService.sendVerificationCode(email);
        String key = "email:verify:" + email;
        String storedCode = redisTemplate.opsForValue().get(key);
//        log.info("key: {}, code: {}",key, storedCode);

        // when
        boolean result = emailVerificationService.verifyCode(email, storedCode);

        // then
        assertThat(result).isTrue();
        assertThat(redisTemplate.opsForValue().get(key)).isNull();
    }


    private void saveMember(String email) {
        AdminMemberRequestDto requestDto = AdminMemberRequestDto.builder()
                .username("username")
                .email(email)
                .password("password")
                .build();
        memberMapper.saveAdminMember(requestDto);
    }

    // private 필드 접근 - ConcurrentHashMap
    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(Object target, String fieldName) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(target);
    }

    // 내부 캐시 접근 - Caffeine Cache
    @SuppressWarnings("unchecked")
    private Map<String, String> getPrivateCacheMap(Object target) throws Exception {
        Field field = target.getClass().getDeclaredField("codeCache");
        field.setAccessible(true);
        Object cache = field.get(target);
        if (cache instanceof com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache) {
            return (Map<String, String>) caffeineCache.asMap();
        }
        throw new IllegalStateException("Cache field is not a Caffeine Cache");
    }
}