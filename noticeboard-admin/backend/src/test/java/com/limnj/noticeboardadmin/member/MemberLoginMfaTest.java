package com.limnj.noticeboardadmin.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limnj.noticeboardadmin.auth.EmailVerificationService;
import com.limnj.noticeboardadmin.auth.MfaTokenService;
import com.limnj.noticeboardadmin.config.SecurityConfig;
import com.limnj.noticeboardadmin.config.handler.CustomAccessDeniedHandler;
import com.limnj.noticeboardadmin.fcm.FcmNotificationService;
import com.limnj.noticeboardadmin.jwt.JwtTokenUtil;
import com.limnj.noticeboardadmin.jwt.RefreshTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 실제 인증 서비스·비밀번호 검증·토큰 저장소를 사용하고 DB와 이메일 발송만 대체한다. */
@WebMvcTest(MemberController.class)
@Import({SecurityConfig.class, CustomAccessDeniedHandler.class, MemberServiceImpl.class,
        LoginPolicyService.class, MfaTokenService.class, MemberLoginMfaTest.PasswordConfig.class})
@DisplayName("1차 로그인 MFA 토큰 응답")
class MemberLoginMfaTest {
    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @SpyBean private MfaTokenService mfaTokens;
    @MockBean private MemberMapper members;
    @MockBean private JwtTokenUtil jwt;
    @MockBean private RefreshTokenService refreshTokens;
    @MockBean private FcmNotificationService notifications;
    @MockBean(name = "concurrentEmailVerificationServiceImpl")
    private EmailVerificationService emailVerification;

    @Test
    @DisplayName("올바른 비밀번호로 로그인하면 서버가 조회한 계정의 MFA 토큰만 발급한다")
    void returnsMfaTokenBoundToVerifiedMember() throws Exception {
        when(members.findMemberByUsername("admin")).thenReturn(Optional.of(member(null)));
        when(emailVerification.sendVerificationCode("owner@example.com")).thenReturn(true);

        String response = mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest("correct-password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(10))
                .andExpect(jsonPath("$.email").value("owner@example.com"))
                .andExpect(jsonPath("$.mfaToken").isNotEmpty())
                .andExpect(jsonPath("$.accessToken").value(nullValue()))
                .andExpect(jsonPath("$.refreshToken").value(nullValue()))
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE))
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("mfaToken").asText();
        assertThat(mfaTokens.findMemberId(token)).contains(10L);
        verify(mfaTokens).issueToken(10L);
        verify(emailVerification).sendVerificationCode("owner@example.com");
        verifyNoInteractions(jwt, refreshTokens, notifications);
    }

    @Test
    @DisplayName("비밀번호가 틀리면 MFA 토큰을 발급하지 않고 실패 횟수를 증가시킨다")
    void rejectsWrongPasswordWithoutIssuingMfaToken() throws Exception {
        when(members.findMemberByUsername("admin")).thenReturn(Optional.of(member(null)));

        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest("wrong-password")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("PASSWORD_MISMATCH"))
                .andExpect(jsonPath("$.mfaToken").doesNotExist());

        verify(members).incrementFailCount(10L);
        verify(mfaTokens, never()).issueToken(anyLong());
        verifyNoInteractions(emailVerification, jwt, refreshTokens, notifications);
    }

    @Test
    @DisplayName("잠긴 계정은 비밀번호가 맞아도 MFA 토큰을 발급하지 않는다")
    void rejectsLockedAccountWithoutIssuingMfaToken() throws Exception {
        when(members.findMemberByUsername("admin"))
                .thenReturn(Optional.of(member(LocalDateTime.now().plusMinutes(5))));

        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest("correct-password")))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.errorCode").value("ACCOUNT_LOCKED"))
                .andExpect(jsonPath("$.mfaToken").doesNotExist());

        verify(mfaTokens, never()).issueToken(anyLong());
        verifyNoInteractions(emailVerification, jwt, refreshTokens, notifications);
    }

    @Test
    @DisplayName("존재하지 않는 계정에는 MFA 토큰을 발급하지 않는다")
    void rejectsUnknownAccountWithoutIssuingMfaToken() throws Exception {
        when(members.findMemberByUsername("admin")).thenReturn(Optional.empty());

        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest("correct-password")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
                .andExpect(jsonPath("$.mfaToken").doesNotExist());

        verify(mfaTokens, never()).issueToken(anyLong());
        verifyNoInteractions(emailVerification, jwt, refreshTokens, notifications);
    }

    @Test
    @DisplayName("이메일 코드 발송이 실패하면 성공 응답과 토큰 쿠키를 반환하지 않는다")
    void preservesEmailDeliveryFailureResponse() throws Exception {
        when(members.findMemberByUsername("admin")).thenReturn(Optional.of(member(null)));
        when(emailVerification.sendVerificationCode("owner@example.com")).thenReturn(false);

        mvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest("correct-password")))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("인증 코드 전송에 실패하였습니다."))
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));

        verifyNoInteractions(jwt, refreshTokens, notifications);
    }

    private AdminMemberResponseDto member(LocalDateTime lockUntil) {
        AdminMemberResponseDto member = new AdminMemberResponseDto();
        ReflectionTestUtils.setField(member, "id", 10L);
        ReflectionTestUtils.setField(member, "username", "admin");
        ReflectionTestUtils.setField(member, "email", "owner@example.com");
        ReflectionTestUtils.setField(member, "password", passwordEncoder.encode("correct-password"));
        ReflectionTestUtils.setField(member, "role", "ROLE_ADMIN");
        ReflectionTestUtils.setField(member, "lockUntil", lockUntil);
        return member;
    }

    private String loginRequest(String password) {
        // 요청의 이메일로 MFA 계정이 결정되지 않는지도 함께 검증한다.
        return """
                {"username":"admin","password":"%s","email":"other@example.com"}
                """.formatted(password);
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class PasswordConfig {
        @Bean
        PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
}
