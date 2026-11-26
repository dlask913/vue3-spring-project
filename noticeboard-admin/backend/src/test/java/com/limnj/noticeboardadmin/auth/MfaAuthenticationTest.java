package com.limnj.noticeboardadmin.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.limnj.noticeboardadmin.exception.GlobalExceptionController;
import com.limnj.noticeboardadmin.fcm.FcmNotificationService;
import com.limnj.noticeboardadmin.jwt.JwtTokenUtil;
import com.limnj.noticeboardadmin.jwt.RefreshTokenService;
import com.limnj.noticeboardadmin.member.AdminMemberResponseDto;
import com.limnj.noticeboardadmin.member.LoginPolicyService;
import com.limnj.noticeboardadmin.member.MemberMapper;
import com.limnj.noticeboardadmin.member.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MFA 토큰을 통한 1차·2차 인증 계정 연결")
class MfaAuthenticationTest {
    @Mock MemberMapper members;
    @Mock JwtTokenUtil jwt;
    @Mock RefreshTokenService refreshTokens;
    @Mock PasswordEncoder passwords;
    @Mock LoginPolicyService loginPolicy;
    @Mock FcmNotificationService notifications;
    @Mock EmailVerificationService emailVerification;
    @Mock QrVerificationService qrVerification;
    private final ObjectMapper json = new ObjectMapper();
    private AtomicLong elapsed;
    private MfaTokenService mfaTokens;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        elapsed = new AtomicLong();
        mfaTokens = new MfaTokenService(elapsed::get);
        // 실제 컨트롤러·회원 서비스·MFA 저장소를 연결하고 외부 연동만 대체한다.
        MemberServiceImpl memberService = new MemberServiceImpl(
                members, jwt, refreshTokens, passwords, loginPolicy, notifications, mfaTokens);
        AuthController controller = new AuthController(memberService, emailVerification, qrVerification, mfaTokens);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionController()).build();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/email-verify", "/qr-verify"})
    @DisplayName("요청에 다른 계정 정보를 넣어도 MFA 계정으로 검증·발급하고 성공한 토큰은 재사용할 수 없다")
    void authenticatesOnlyPendingMemberAndRejectsReplay(String endpoint) throws Exception {
        stubMember();
        stubVerification(endpoint, true);
        when(jwt.generateToken("owner")).thenReturn("owner-access");
        when(refreshTokens.generateRefreshToken("owner")).thenReturn("owner-refresh");
        String token = mfaTokens.issueToken(10L);
        String body = request(token, "123456", "browser-token");

        mvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memberId").value(10))
                .andExpect(jsonPath("$.username").value("owner"))
                .andExpect(jsonPath("$.accessToken").value("owner-access"))
                .andExpect(cookie().value("refreshToken", "owner-refresh"));
        mvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("MFA_TOKEN_INVALID"))
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));

        verify(jwt).generateToken("owner");
        verify(refreshTokens).generateRefreshToken("owner");
        verify(members, never()).findMemberByUsername(any());
        verify(notifications).updateFcmTokenForUser(argThat(dto ->
                dto.getUserId().equals(10L) && dto.getToken().equals("browser-token")));
        assertThat(mfaTokens.findMemberId(token)).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({"/email-verify,missing", "/email-verify,unknown", "/email-verify,expired",
            "/qr-verify,missing", "/qr-verify,unknown", "/qr-verify,expired",
            "/resend-code,missing", "/resend-code,unknown", "/resend-code,expired"})
    @DisplayName("누락·위조·만료된 MFA 토큰은 계정 조회와 코드 검증·재발송 전에 거절한다")
    void rejectsInvalidMfaBeforeAuthentication(String endpoint, String type) throws Exception {
        String token = switch (type) {
            case "missing" -> null;
            case "unknown" -> "unknown-token";
            default -> mfaTokens.issueToken(10L);
        };
        if (type.equals("expired")) elapsed.set(Duration.ofMinutes(5).toNanos());
        mvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON).content(request(token, "123456", null)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("MFA_TOKEN_INVALID"))
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
        verifyNoInteractions(members, emailVerification, qrVerification, jwt, refreshTokens, notifications);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/email-verify", "/qr-verify"})
    @DisplayName("코드가 틀리면 토큰 발급 없이 거절하고 MFA 대기 상태는 유지한다")
    void wrongCodeDoesNotCompleteAuthentication(String endpoint) throws Exception {
        stubMember();
        stubVerification(endpoint, false);
        String token = mfaTokens.issueToken(10L);
        mvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON).content(request(token, "123456", null)))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
        assertThat(mfaTokens.findMemberId(token)).contains(10L);
        verifyNoInteractions(jwt, refreshTokens, notifications);
    }

    @Test
    @DisplayName("재발송은 MFA 계정의 이메일만 사용하고 토큰 유효기간을 연장하지 않는다")
    void resendsOnlyToPendingMemberWithoutExtendingExpiry() throws Exception {
        stubMember();
        when(emailVerification.sendVerificationCode("owner@example.com")).thenReturn(true);
        String token = mfaTokens.issueToken(10L);
        elapsed.set(Duration.ofMinutes(4).toNanos());
        mvc.perform(post("/resend-code").contentType(MediaType.APPLICATION_JSON).content(request(token, "123456", null)))
                .andExpect(status().isNoContent());
        verify(emailVerification).sendVerificationCode("owner@example.com");
        assertThat(mfaTokens.findMemberId(token)).contains(10L);
        elapsed.set(Duration.ofMinutes(5).toNanos());
        assertThat(mfaTokens.findMemberId(token)).isEmpty();
        verifyNoInteractions(jwt, refreshTokens, notifications);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "12345", "abcdef", "1234567"})
    @DisplayName("잘못된 OTP 입력 형식은 숫자 변환 예외 대신 400으로 거절한다")
    void rejectsMalformedCode(String code) throws Exception {
        mvc.perform(post("/qr-verify").contentType(MediaType.APPLICATION_JSON)
                        .content(request(mfaTokens.issueToken(10L), code, null)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(members, qrVerification, jwt, refreshTokens);
    }

    private void stubMember() {
        AdminMemberResponseDto member = new AdminMemberResponseDto();
        ReflectionTestUtils.setField(member, "id", 10L);
        ReflectionTestUtils.setField(member, "username", "owner");
        ReflectionTestUtils.setField(member, "email", "owner@example.com");
        when(members.findMemberById(10L)).thenReturn(Optional.of(member));
    }

    private void stubVerification(String endpoint, boolean valid) {
        if (endpoint.equals("/email-verify")) {
            when(emailVerification.verifyCode("owner@example.com", "123456")).thenReturn(valid);
        } else {
            when(qrVerification.verifyCode("owner@example.com", 123456)).thenReturn(valid);
        }
    }

    private String request(String token, String code, String fcmToken) throws Exception {
        return json.writeValueAsString(json.createObjectNode().put("mfaToken", token)
                .put("authenticationCode", code).put("fcmToken", fcmToken)
                .put("username", "other-account").put("email", "other@example.com"));
    }
}
