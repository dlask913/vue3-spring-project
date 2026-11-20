package com.limnj.noticeboardadmin.member;

import com.limnj.noticeboardadmin.auth.EmailVerificationService;
import com.limnj.noticeboardadmin.auth.MfaTokenService;
import com.limnj.noticeboardadmin.config.SecurityConfig;
import com.limnj.noticeboardadmin.config.filter.JwtExceptionFilter;
import com.limnj.noticeboardadmin.config.filter.JwtTokenFilter;
import com.limnj.noticeboardadmin.config.handler.CustomAccessDeniedHandler;
import com.limnj.noticeboardadmin.exception.GlobalExceptionController;
import com.limnj.noticeboardadmin.fcm.FcmNotificationMapper;
import com.limnj.noticeboardadmin.fcm.FcmNotificationService;
import com.limnj.noticeboardadmin.jwt.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** 실제 Security 필터, 컨트롤러, 서비스, MyBatis, PostgreSQL을 연결한다.
 * 로그아웃에서 사용하지 않는 회원 조회·메일 발송만 대체한다. DB 스키마와 데이터는 모두 롤백한다. */
@SpringJUnitConfig(classes = LogoutIntegrationTest.TestConfig.class,
        initializers = ConfigDataApplicationContextInitializer.class)
@WebAppConfiguration
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_TEST_DB:${POSTGRES_DB:noticeboard}}",
        "httponly-secure-option=true"
})
@Transactional
@DisplayName("로그아웃 HTTP 경로와 PostgreSQL 통합 테스트")
class LogoutIntegrationTest {
    private static final String TEST_KEY = "logout-integration-test-only-signing-key-0123456789";
    @Autowired private WebApplicationContext context;
    @Autowired private RefreshTokenService tokens;
    @Autowired private JdbcTemplate jdbc;
    private MockMvc mvc;
    private String schema;
    private String ownerToken;
    private String otherToken;

    @BeforeEach
    void setUp() throws Exception {
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isTrue();
        schema = "logout_it_" + UUID.randomUUID().toString().replace("-", "");
        jdbc.execute("CREATE SCHEMA " + schema);
        jdbc.execute("SET LOCAL search_path TO " + schema);
        assertThat(jdbc.queryForObject("SELECT current_schema()", String.class)).isEqualTo(schema);
        String ddl = new ClassPathResource("schema-postgresql.sql")
                .getContentAsString(StandardCharsets.UTF_8)
                .replaceFirst("(?m)^BEGIN;\\s*$", "")
                .replaceFirst("(?m)^COMMIT;\\s*$", "");
        jdbc.execute(ddl);
        jdbc.update("INSERT INTO members(id, username, user_type) VALUES (1, 'owner', 'ADMIN'), (2, 'other', 'ADMIN')");
        jdbc.update("INSERT INTO fcm_tokens(user_id, token) VALUES (1, 'own-device'), (1, 'second-device'), (2, 'other-device')");
        ownerToken = tokens.generateRefreshToken("owner");
        otherToken = tokens.generateRefreshToken("other");
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @AfterTransaction
    void confirmsSchemaRollback() {
        if (schema != null) {
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM pg_namespace WHERE nspname = ?",
                    Long.class, schema)).isZero();
        }
    }

    @Test
    @DisplayName("요청 username을 무시하고 쿠키 계정의 토큰과 해당 FCM 연결만 해제한다")
    void revokesCookieOwnerAndClearsBrowserCookie() throws Exception {
        mvc.perform(logout(ownerToken).contentType("application/json")
                        .content("{\"username\":\"other\",\"fcmToken\":\"own-device\"}"))
                .andExpect(status().isNoContent())
                .andExpect(cookie().value("refreshToken", ""))
                .andExpect(cookie().path("refreshToken", "/"))
                .andExpect(cookie().maxAge("refreshToken", 0))
                .andExpect(cookie().httpOnly("refreshToken", true))
                .andExpect(cookie().secure("refreshToken", true))
                .andExpect(header().string(HttpHeaders.SET_COOKIE, containsString("SameSite=Lax")));

        assertThat(tokenCount(ownerToken)).isZero();
        assertThat(tokenCount(otherToken)).isEqualTo(1);
        assertThat(deviceOwner("own-device")).isNull();
        assertThat(deviceOwner("second-device")).isEqualTo(1L);
        assertThat(deviceOwner("other-device")).isEqualTo(2L);
        mvc.perform(post("/refresh").cookie(new Cookie("refreshToken", ownerToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("REFRESH_TOKEN_INVALID"));
    }

    @Test
    @DisplayName("타인의 FCM 토큰을 보내도 타인 기기의 사용자 연결을 해제하지 않는다")
    void preservesAnotherUsersDevice() throws Exception {
        mvc.perform(logout(ownerToken).contentType("application/json")
                        .content("{\"fcmToken\":\"other-device\"}"))
                .andExpect(status().isNoContent());
        assertThat(tokenCount(ownerToken)).isZero();
        assertThat(deviceOwner("other-device")).isEqualTo(2L);
        assertThat(deviceOwner("own-device")).isEqualTo(1L);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "unknown-token")
    @DisplayName("쿠키가 없거나 빈 값·미등록 토큰이면 DB 변경 없이 쿠키만 만료시킨다")
    void unknownCookieCannotRevokeAccount(String token) throws Exception {
        mvc.perform(logout(token).contentType("application/json")
                        .content("{\"username\":\"owner\",\"fcmToken\":\"own-device\"}"))
                .andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("refreshToken", 0));
        assertThat(tokenCount(ownerToken)).isEqualTo(1);
        assertThat(tokenCount(otherToken)).isEqualTo(1);
        assertThat(deviceOwner("own-device")).isEqualTo(1L);
    }

    @Test
    @DisplayName("교체된 이전 토큰으로 로그아웃해도 새 로그인 토큰과 FCM 연결은 유지한다")
    void staleCookieCannotRevokeNewLogin() throws Exception {
        String newToken = tokens.generateRefreshToken("owner");
        mvc.perform(logout(ownerToken).contentType("application/json").content("{\"fcmToken\":\"own-device\"}"))
                .andExpect(status().isNoContent());
        assertThat(tokenCount(newToken)).isEqualTo(1);
        assertThat(deviceOwner("own-device")).isEqualTo(1L);
    }

    @Test
    @DisplayName("만료된 Refresh Token도 일치하는 DB 행과 본인 FCM 연결을 정리할 수 있다")
    void cleansUpExpiredRefreshToken() throws Exception {
        jdbc.update("UPDATE refresh_token SET expiry_date = NOW() - INTERVAL '1 second' WHERE token = ?", ownerToken);
        mvc.perform(logout(ownerToken).contentType("application/json").content("{\"fcmToken\":\"own-device\"}"))
                .andExpect(status().isNoContent());
        assertThat(tokenCount(ownerToken)).isZero();
        assertThat(deviceOwner("own-device")).isNull();
    }

    @Test
    @DisplayName("요청 본문 없이 반복 로그아웃해도 성공하며 타인의 토큰은 유지한다")
    void logoutIsIdempotentWithoutBody() throws Exception {
        mvc.perform(logout(ownerToken)).andExpect(status().isNoContent());
        mvc.perform(logout(ownerToken)).andExpect(status().isNoContent())
                .andExpect(cookie().maxAge("refreshToken", 0));
        assertThat(tokenCount(ownerToken)).isZero();
        assertThat(tokenCount(otherToken)).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(strings = {"expired", "malformed"})
    @DisplayName("만료되거나 잘못된 Access Token이 있어도 쿠키로 로그아웃할 수 있다")
    void logoutDoesNotRequireValidAccessToken(String type) throws Exception {
        String access = "expired".equals(type)
                ? Jwts.builder().setSubject("owner").setExpiration(new Date(0))
                    .signWith(Keys.hmacShaKeyFor(TEST_KEY.getBytes(StandardCharsets.UTF_8))).compact()
                : "not-a-jwt";
        mvc.perform(logout(ownerToken).header(HttpHeaders.AUTHORIZATION, "Bearer " + access))
                .andExpect(status().isNoContent());
        assertThat(tokenCount(ownerToken)).isZero();
        // 같은 잘못된 Access Token은 보호된 다른 API에서는 계속 거부되어야 한다.
        mvc.perform(get("/notices").servletPath("/notices").header(HttpHeaders.AUTHORIZATION, "Bearer " + access))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET 로그아웃 요청은 토큰을 폐기하거나 쿠키를 삭제하지 않는다")
    void getDoesNotLogout() throws Exception {
        mvc.perform(get("/logout").servletPath("/logout").cookie(new Cookie("refreshToken", ownerToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(header().doesNotExist(HttpHeaders.SET_COOKIE));
        assertThat(tokenCount(ownerToken)).isEqualTo(1);
    }

    private MockHttpServletRequestBuilder logout(String token) {
        var request = post("/logout").servletPath("/logout");
        return token == null ? request : request.cookie(new Cookie("refreshToken", token));
    }

    private int tokenCount(String token) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM refresh_token WHERE token = ?", Integer.class, token);
    }

    private Long deviceOwner(String token) {
        return jdbc.queryForObject("SELECT user_id FROM fcm_tokens WHERE token = ?", Long.class, token);
    }

    @Configuration(proxyBeanMethods = false)
    @EnableWebMvc
    @EnableTransactionManagement
    @Import({SecurityConfig.class, JwtTokenFilter.class, JwtExceptionFilter.class, CustomAccessDeniedHandler.class,
            MemberController.class, MemberServiceImpl.class, LoginPolicyService.class, MfaTokenService.class,
            RefreshTokenServiceImpl.class, TokenController.class, FcmNotificationService.class, GlobalExceptionController.class})
    static class TestConfig {
        @Bean
        DataSource dataSource(Environment environment) {
            return new UnpooledDataSource("org.postgresql.Driver",
                    environment.getRequiredProperty("spring.datasource.url").replace("jdbc:log4jdbc:", "jdbc:"),
                    environment.getRequiredProperty("spring.datasource.username"),
                    environment.getRequiredProperty("spring.datasource.password"));
        }

        @Bean
        SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
            var configuration = new org.apache.ibatis.session.Configuration();
            configuration.setMapUnderscoreToCamelCase(true);
            var factory = new SqlSessionFactoryBean();
            factory.setDataSource(dataSource);
            factory.setConfiguration(configuration);
            factory.setTypeAliasesPackage("com.limnj.noticeboardadmin.jwt,com.limnj.noticeboardadmin.fcm");
            factory.setMapperLocations(new ClassPathResource("mapper/TokenMapper.xml"),
                    new ClassPathResource("mapper/FcmNotificationMapper.xml"));
            return factory.getObject();
        }

        @Bean
        MapperFactoryBean<TokenMapper> tokenMapper(SqlSessionFactory factory) {
            var mapper = new MapperFactoryBean<>(TokenMapper.class);
            mapper.setSqlSessionFactory(factory);
            return mapper;
        }

        @Bean
        MapperFactoryBean<FcmNotificationMapper> fcmMapper(SqlSessionFactory factory) {
            var mapper = new MapperFactoryBean<>(FcmNotificationMapper.class);
            mapper.setSqlSessionFactory(factory);
            return mapper;
        }

        @Bean PlatformTransactionManager transactionManager(DataSource source) { return new DataSourceTransactionManager(source); }
        @Bean JdbcTemplate jdbcTemplate(DataSource source) { return new JdbcTemplate(source); }
        @Bean JwtTokenUtil jwtTokenUtil() { return new JwtTokenUtil(TEST_KEY); }
        @Bean MemberMapper memberMapper() { return mock(MemberMapper.class); }
        @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
        @Bean(name = "concurrentEmailVerificationServiceImpl")
        EmailVerificationService emailVerificationService() { return mock(EmailVerificationService.class); }
    }
}
