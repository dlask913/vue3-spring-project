package com.limnj.noticeboardadmin.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("2차 인증 대기 토큰 관리")
class MfaTokenServiceTest {
    private AtomicLong elapsedNanos;
    private MfaTokenService service;

    @BeforeEach
    void setUp() {
        elapsedNanos = new AtomicLong();
        service = new MfaTokenService(elapsedNanos::get);
    }

    @Test
    @DisplayName("발급할 때마다 별도 토큰을 생성하고 각 계정에 연결한다")
    void issuesDistinctTokensBoundToMembers() {
        String first = service.issueToken(1L);
        String second = service.issueToken(2L);
        String anotherLogin = service.issueToken(1L);

        assertThat(first).matches("[A-Za-z0-9_-]{43}").isNotEqualTo(second).isNotEqualTo(anotherLogin);
        assertThat(service.findMemberId(first)).contains(1L);
        assertThat(service.findMemberId(second)).contains(2L);
        assertThat(service.findMemberId(anotherLogin)).contains(1L);
    }

    @Test
    @DisplayName("토큰을 중간에 조회해도 발급 후 5분이 되면 만료된다")
    void expiresFiveMinutesAfterIssuanceWithoutExtendingOnRead() {
        String token = service.issueToken(1L);
        elapsedNanos.set(Duration.ofMinutes(4).toNanos());
        assertThat(service.findMemberId(token)).contains(1L);

        elapsedNanos.set(Duration.ofMinutes(5).toNanos());
        assertThat(service.findMemberId(token)).isEmpty();
    }

    @Test
    @DisplayName("누락되거나 등록되지 않은 토큰으로는 계정을 조회할 수 없다")
    void rejectsMissingAndUnknownTokens() {
        assertThat(service.findMemberId(null)).isEmpty();
        assertThat(service.findMemberId(" ")).isEmpty();
        assertThat(service.findMemberId("unknown-token")).isEmpty();
    }
}
