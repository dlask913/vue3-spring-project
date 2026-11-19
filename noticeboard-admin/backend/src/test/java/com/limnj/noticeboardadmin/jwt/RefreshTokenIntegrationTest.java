package com.limnj.noticeboardadmin.jwt;

import com.limnj.noticeboardadmin.exception.BizException;
import com.limnj.noticeboardadmin.exception.ErrorCode;
import com.limnj.noticeboardadmin.exception.GlobalExceptionController;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 실제 서비스,MyBatis,PostgreSQL,JWT 서명으로 재발급을 검증한다.
 * 기존 FCM 통합 테스트와 동일하게 로컬 DB의 격리된 스키마를 사용하고 DDL과 데이터를 롤백한다.
 * MockMvc는 쿠키 바인딩과 응답을 검증하며, 이 테스트에는 Security 필터를 연결하지 않는다.
 */
@SpringJUnitConfig(classes = RefreshTokenIntegrationTest.TestConfig.class,
        initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_TEST_DB:${POSTGRES_DB:noticeboard}}")
@Transactional
@DisplayName("UUID Refresh Token 재발급과 PostgreSQL 통합 테스트")
class RefreshTokenIntegrationTest {
    @Autowired private RefreshTokenService service;
    @Autowired private JwtTokenUtil jwt;
    @Autowired private TokenController controller;
    @Autowired private JdbcTemplate jdbc;
    private MockMvc mvc;
    private String testSchema;

    @BeforeEach
    void setUp() throws Exception {
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isTrue();
        testSchema = "refresh_it_" + UUID.randomUUID().toString().replace("-", "");
        jdbc.execute("CREATE SCHEMA " + testSchema);
        jdbc.execute("SET LOCAL search_path TO " + testSchema);
        assertThat(jdbc.queryForObject("SELECT current_schema()", String.class)).isEqualTo(testSchema);
        String schemaSql = new ClassPathResource("schema-postgresql.sql")
                .getContentAsString(StandardCharsets.UTF_8)
                .replaceFirst("(?m)^BEGIN;\\s*$", "")
                .replaceFirst("(?m)^COMMIT;\\s*$", "");
        jdbc.execute(schemaSql);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionController()).build();
    }

    @AfterTransaction
    void verifiesSchemaWasRolledBack() {
        if (testSchema != null) {
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM pg_namespace WHERE nspname = ?",
                    Long.class, testSchema)).isZero();
        }
    }

    @Test
    @DisplayName("UUID 쿠키로 재발급하면 DB에 연결된 계정의 유효한 Access Token을 반환한다")
    void refreshesAccessTokenForStoredAccount() throws Exception {
        String token = service.generateRefreshToken("owner");
        service.generateRefreshToken("another-user");
        assertThat(UUID.fromString(token).toString()).isEqualTo(token);

        String accessToken = mvc.perform(post("/refresh")
                        .cookie(new Cookie("refreshToken", token))
                        .contentType("application/json").content("{\"username\":\"another-user\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // 실제 JWT 서명 검증을 거쳐 요청 본문이 아닌 DB 계정으로 발급됐는지 확인한다.
        assertThat(jwt.getUsernameFromToken(accessToken)).isEqualTo("owner");
        assertThat(jwt.getExpirationDateFromToken(accessToken)).isAfter(new java.util.Date());
    }

    @Test
    @DisplayName("DB에 등록되지 않은 UUID는 401로 거부한다")
    void rejectsUnknownToken() throws Exception {
        service.generateRefreshToken("owner");
        assertRejected(UUID.randomUUID().toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    @DisplayName("만료 시각이 지났거나 현재 DB 시각과 같으면 재발급을 거부한다")
    void rejectsExpiredTokenIncludingBoundary(int seconds) throws Exception {
        String token = service.generateRefreshToken("owner");
        // PostgreSQL NOW()는 트랜잭션 안에서 고정되므로 경계값을 대기 없이 검증할 수 있다.
        jdbc.update("UPDATE refresh_token SET expiry_date = NOW() + (? * INTERVAL '1 second') WHERE token = ?",
                seconds, token);
        assertRejected(token);
    }

    @Test
    @DisplayName("폐기된 Refresh Token으로는 Access Token을 발급할 수 없다")
    void rejectsRevokedToken() throws Exception {
        String token = service.generateRefreshToken("owner");
        service.expireRefreshToken("owner");
        assertRejected(token);
    }

    @Test
    @DisplayName("재로그인으로 토큰이 교체되면 이전 Refresh Token은 사용할 수 없다")
    void rejectsReplacedToken() throws Exception {
        String previous = service.generateRefreshToken("owner");
        String current = service.generateRefreshToken("owner");
        assertThat(jwt.getUsernameFromToken(service.generateNewAccessToken(current))).isEqualTo("owner");
        assertRejected(previous);
    }

    @Test
    @DisplayName("유효하게 서명된 Access Token도 DB에 등록된 Refresh Token을 대신할 수 없다")
    void rejectsAccessTokenAsRefreshToken() throws Exception {
        service.generateRefreshToken("owner");
        assertRejected(jwt.generateToken("owner"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    @DisplayName("누락되거나 빈 토큰으로 서비스가 호출되면 명시적인 인증 오류를 반환한다")
    void rejectsMissingToken(String token) {
        assertThatThrownBy(() -> service.generateNewAccessToken(token))
                .isInstanceOfSatisfying(BizException.class,
                        exception -> assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.REFRESH_TOKEN_INVALID));
    }

    private void assertRejected(String token) throws Exception {
        mvc.perform(post("/refresh").cookie(new Cookie("refreshToken", token)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("REFRESH_TOKEN_INVALID"));
    }

    @Configuration(proxyBeanMethods = false)
    @EnableTransactionManagement
    @Import({RefreshTokenServiceImpl.class, TokenController.class})
    static class TestConfig {
        @Bean
        DataSource dataSource(Environment environment) {
            String url = environment.getRequiredProperty("spring.datasource.url")
                    .replace("jdbc:log4jdbc:", "jdbc:");
            return new UnpooledDataSource("org.postgresql.Driver", url,
                    environment.getRequiredProperty("spring.datasource.username"),
                    environment.getRequiredProperty("spring.datasource.password"));
        }

        @Bean
        JwtTokenUtil jwtTokenUtil() {
            // 테스트 전용 서명 키이며 운영 환경의 JWT 비밀키를 사용하지 않는다.
            return new JwtTokenUtil("refresh-integration-test-signing-key-only-0123456789");
        }

        @Bean
        SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
            var configuration = new org.apache.ibatis.session.Configuration();
            configuration.setMapUnderscoreToCamelCase(true);
            var factory = new SqlSessionFactoryBean();
            factory.setDataSource(dataSource);
            factory.setConfiguration(configuration);
            factory.setTypeAliasesPackage("com.limnj.noticeboardadmin.jwt");
            factory.setMapperLocations(new ClassPathResource("mapper/TokenMapper.xml"));
            return factory.getObject();
        }

        @Bean
        MapperFactoryBean<TokenMapper> tokenMapper(SqlSessionFactory sqlSessionFactory) {
            var factory = new MapperFactoryBean<>(TokenMapper.class);
            factory.setSqlSessionFactory(sqlSessionFactory);
            return factory;
        }

        @Bean
        PlatformTransactionManager transactionManager(DataSource dataSource) {
            return new DataSourceTransactionManager(dataSource);
        }

        @Bean
        JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }
    }
}
