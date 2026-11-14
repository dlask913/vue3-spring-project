package com.limnj.noticeboardadmin.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

/** 1차 인증을 통과한 계정과 2차 인증 대기 토큰을 연결한다 */
@Service
public class MfaTokenService {
    private static final Duration TOKEN_VALIDITY = Duration.ofMinutes(5);
    private final SecureRandom secureRandom = new SecureRandom();
    private final Cache<String, Long> pendingMembers;

    public MfaTokenService() {
        this(Ticker.systemTicker());
    }

    // 테스트에서는 실제 대기 없이 만료 시각을 검증할 수 있는 시계를 전달한다
    MfaTokenService(Ticker ticker) {
        pendingMembers = Caffeine.newBuilder()
                .expireAfterWrite(TOKEN_VALIDITY)
                .maximumSize(10_000)
                .ticker(ticker)
                .build();
    }

    public String issueToken(Long memberId) {
        Objects.requireNonNull(memberId, "MFA 토큰에는 회원 ID가 필요합니다.");
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        pendingMembers.put(token, memberId);
        return token;
    }

    public Optional<Long> findMemberId(String mfaToken) {
        if (mfaToken == null || mfaToken.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(pendingMembers.getIfPresent(mfaToken));
    }
}
