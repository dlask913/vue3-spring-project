package com.limnj.noticeboardadmin.auth;

import com.limnj.noticeboardadmin.config.SecurityConfig;
import com.limnj.noticeboardadmin.config.handler.CustomAccessDeniedHandler;
import com.limnj.noticeboardadmin.exception.BizException;
import com.limnj.noticeboardadmin.exception.ErrorCode;
import com.limnj.noticeboardadmin.jwt.JwtTokenUtil;
import com.limnj.noticeboardadmin.member.LoginResponseDto;
import com.limnj.noticeboardadmin.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 실제 컨트롤러,JSON 직렬화,보안 필터를 사용하고 인증 및 토큰 발급 서비스는 대체 */
@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, CustomAccessDeniedHandler.class})
@DisplayName("2차 인증 성공 후 Refresh Token 쿠키 전달")
class AuthRefreshTokenCookieTest {
    @Autowired private MockMvc mvc;
    @Autowired private AuthController controller;
    @MockBean private MemberService members;
    @MockBean private JwtTokenUtil jwt;
    @MockBean(name = "concurrentEmailVerificationServiceImpl")
    private EmailVerificationService emailVerification;
    @MockBean private QrVerificationService qrVerification;

    private static final String REQUEST = """
            {"username":"admin","email":"owner@example.com","authenticationCode":"123456"}
            """;

    @ParameterizedTest(name = "{0}, Secure={1}")
    @CsvSource({"/email-verify, false", "/email-verify, true", "/qr-verify, false", "/qr-verify, true"})
    @DisplayName("이메일·OTP 인증 성공 시 발급된 토큰을 쿠키로만 전달하고 환경별 Secure 설정을 적용한다")
    void returnsRefreshTokenOnlyInCookie(String endpoint, boolean secure) throws Exception {
        ReflectionTestUtils.setField(controller, "secureCookie", secure);
        stubVerification(endpoint, true);
        when(members.generateSecondaryAuthToken(any())).thenReturn(LoginResponseDto.builder()
                .memberId(10L)
                .username("admin")
                .email("owner@example.com")
                .role("ROLE_ADMIN")
                .accessToken("issued-access-token")
                .refreshToken("issued-refresh-token")
                .build());

        mvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON).content(REQUEST))
                .andExpect(status().isOk())
                .andExpect(cookie().value("refreshToken", "issued-refresh-token"))
                .andExpect(cookie().httpOnly("refreshToken", true))
                .andExpect(cookie().secure("refreshToken", secure))
                .andExpect(cookie().path("refreshToken", "/"))
                .andExpect(cookie().maxAge("refreshToken", 604800))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Lax")))
                .andExpect(jsonPath("$.refreshToken").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.accessToken").value("issued-access-token"))
                .andExpect(jsonPath("$.memberId").value(10))
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.email").value("owner@example.com"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/email-verify", "/qr-verify"})
    @DisplayName("인증 코드가 틀리면 로그인 토큰을 발급하지 않고 쿠키도 설정하지 않는다")
    void invalidCodeDoesNotSetCookie(String endpoint) throws Exception {
        stubVerification(endpoint, false);

        mvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON).content(REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));

        verifyNoInteractions(members);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/email-verify", "/qr-verify"})
    @DisplayName("코드 검증 후 계정 조회가 실패해도 Refresh Token 쿠키를 설정하지 않는다")
    void tokenIssuanceFailureDoesNotSetCookie(String endpoint) throws Exception {
        stubVerification(endpoint, true);
        when(members.generateSecondaryAuthToken(any()))
                .thenThrow(new BizException(ErrorCode.MEMBER_NOT_FOUND));

        mvc.perform(post(endpoint).contentType(MediaType.APPLICATION_JSON).content(REQUEST))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("MEMBER_NOT_FOUND"))
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
    }

    private void stubVerification(String endpoint, boolean valid) {
        if ("/email-verify".equals(endpoint)) {
            when(emailVerification.verifyCode("owner@example.com", "123456")).thenReturn(valid);
        } else {
            when(qrVerification.verifyCode("owner@example.com", 123456)).thenReturn(valid);
        }
    }
}
