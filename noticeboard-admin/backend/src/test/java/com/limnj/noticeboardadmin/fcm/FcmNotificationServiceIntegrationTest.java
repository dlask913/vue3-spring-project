package com.limnj.noticeboardadmin.fcm;

import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 실제 Spring 서비스, MyBatis 매퍼, 실행 중인 PostgreSQL을 연결하는 통합 테스트다.
 * application.yml의 test 프로필과 로컬 비밀 설정에서 DB 접속 정보를 읽는다.
 * POSTGRES_TEST_DB가 있으면 사용하고, 없으면 POSTGRES_DB(기본값 noticeboard)에 접속한다.
 * 접속한 DB 안에 테스트마다 임시 스키마를 생성하므로 DB 생성 권한은 필요 없다.
 * 기존 테이블은 사용하지 않으며 스키마와 데이터 모두 종료 시 롤백한다.
 * 푸시 발송은 호출하지 않으므로 실제 Firebase 연결이나 기기 토큰은 필요 없다.
 */
@SpringJUnitConfig(classes = FcmNotificationServiceIntegrationTest.TestConfig.class,
        initializers = ConfigDataApplicationContextInitializer.class)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.datasource.url=jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_TEST_DB:${POSTGRES_DB:noticeboard}}")
@Transactional
@DisplayName("FCM 토큰 저장 서비스와 PostgreSQL 통합 테스트")
class FcmNotificationServiceIntegrationTest {

    @Autowired
    private FcmNotificationService service;

    @Autowired
    private FcmNotificationMapper mapper;

    @Autowired
    private JdbcTemplate jdbc;

    private String testSchema;

    @BeforeEach
    void createIsolatedSchema() throws Exception {
        // 스키마 생성까지 테스트 트랜잭션에 포함해야 실패하더라도 DB에 남지 않는다.
        assertThat(TransactionSynchronizationManager.isActualTransactionActive()).isTrue();
        testSchema = "fcm_it_" + UUID.randomUUID().toString().replace("-", "");
        jdbc.execute("CREATE SCHEMA " + testSchema);
        jdbc.execute("SET LOCAL search_path TO " + testSchema);
        assertThat(jdbc.queryForObject("SELECT current_schema()", String.class)).isEqualTo(testSchema);

        // 공용 DDL의 최상위 BEGIN/COMMIT만 제거하여 테스트의 롤백 경계를 유지한다.
        String schemaSql = new ClassPathResource("schema-postgresql.sql")
                .getContentAsString(StandardCharsets.UTF_8)
                .replaceFirst("(?m)^BEGIN;\\s*$", "")
                .replaceFirst("(?m)^COMMIT;\\s*$", "");
        jdbc.execute(schemaSql);
    }

    @AfterTransaction
    void verifiesSchemaWasRolledBack() {
        if (testSchema != null) {
            assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM pg_namespace WHERE nspname = ?",
                    Long.class, testSchema)).isZero();
        }
    }

    @Test
    @DisplayName("익명 기기의 토큰과 기기 종류가 실제 DB에 저장된다")
    void savesAnonymousDevice() {
        service.updateFcmTokenForUser(request(null, "anonymous-device"));

        assertThat(savedTokens()).singleElement().satisfies(row -> {
            assertThat(row.id()).isPositive();
            assertThat(row.userId()).isNull();
            assertThat(row.token()).isEqualTo("anonymous-device");
            assertThat(row.deviceType()).isEqualTo("WEB");
        });
    }

    @ParameterizedTest(name = "사용자 ID={0}인 토큰을 두 번 등록해도 한 행만 유지한다")
    @NullSource
    @ValueSource(longs = 1L)
    @DisplayName("익명, 로그인 상태에서 같은 토큰을 재등록해도 중복 행이 생성되지 않는다")
    void doesNotDuplicateRegistration(Long userId) {
        service.updateFcmTokenForUser(request(userId, "same-device"));
        TokenRow original = savedTokens().get(0);

        service.updateFcmTokenForUser(request(userId, "same-device"));

        assertThat(savedTokens()).containsExactly(original);
    }

    @Test
    @DisplayName("익명 토큰으로 로그인하면 기존 행에 사용자 ID가 연결된다")
    void bindsAnonymousDeviceToUser() {
        service.updateFcmTokenForUser(request(null, "login-device"));
        long originalId = savedTokens().get(0).id();

        service.updateFcmTokenForUser(request(1L, "login-device"));

        assertThat(savedTokens()).containsExactly(new TokenRow(originalId, 1L, "login-device", "WEB"));
        assertThat(mapper.findFcmTokenByUserId(1L))
                .extracting(FcmTokenResponseDto::getToken).containsExactly("login-device");
    }

    @Test
    @DisplayName("이미 연결된 토큰을 익명으로 재등록해도 기존 사용자가 유지된다")
    void preservesUserOnAnonymousRegistration() {
        service.updateFcmTokenForUser(request(1L, "owned-device"));
        TokenRow original = savedTokens().get(0);

        service.updateFcmTokenForUser(request(null, "owned-device"));

        // 매퍼의 COALESCE가 기존 user_id를 유지하는지 실제 SQL 결과로 확인한다.
        assertThat(savedTokens()).containsExactly(original);
        assertThat(mapper.existFcmTokenByUserIdAndToken(1L, "owned-device")).isTrue();
    }

    @Test
    @DisplayName("같은 기기로 다른 사용자가 로그인하면 기존 행의 사용자만 변경된다")
    void rebindsDeviceToAnotherUser() {
        service.updateFcmTokenForUser(request(1L, "shared-device"));
        long originalId = savedTokens().get(0).id();

        service.updateFcmTokenForUser(request(2L, "shared-device"));

        assertThat(savedTokens()).containsExactly(new TokenRow(originalId, 2L, "shared-device", "WEB"));
        assertThat(mapper.findFcmTokenByUserId(1L)).isEmpty();
        assertThat(mapper.findFcmTokenByUserId(2L))
                .extracting(FcmTokenResponseDto::getToken).containsExactly("shared-device");
    }

    @Test
    @DisplayName("한 사용자에게 여러 기기를 등록해도 다른 사용자의 기기와 구분된다")
    void storesMultipleDevicesPerUser() {
        service.updateFcmTokenForUser(request(1L, "desktop"));
        service.updateFcmTokenForUser(request(1L, "mobile"));
        service.updateFcmTokenForUser(request(2L, "another-user-device"));

        assertThat(savedTokens()).hasSize(3);
        assertThat(mapper.findFcmTokenByUserId(1L))
                .extracting(FcmTokenResponseDto::getToken).containsExactlyInAnyOrder("desktop", "mobile");
        assertThat(mapper.findFcmTokenByUserId(2L))
                .extracting(FcmTokenResponseDto::getToken).containsExactly("another-user-device");
    }

    @Test
    @DisplayName("로그아웃 대상 기기만 사용자 연결을 해제하고 전체 공지용 토큰은 유지한다")
    void unbindsOnlyLoggedOutDevice() {
        service.updateFcmTokenForUser(request(1L, "logout-device"));
        service.updateFcmTokenForUser(request(1L, "other-device"));
        service.updateFcmTokenForUser(request(2L, "another-user-device"));
        long originalId = savedTokens().stream()
                .filter(row -> row.token().equals("logout-device")).findFirst().orElseThrow().id();

        // HTTP /logout 경로가 아닌 FCM 연결 해제 서비스의 DB 처리를 검증한다.
        service.unbindFcmToken("logout-device");

        assertThat(savedTokens()).hasSize(3)
                .contains(new TokenRow(originalId, null, "logout-device", "WEB"));
        assertThat(mapper.findFcmTokenByUserId(1L))
                .extracting(FcmTokenResponseDto::getToken).containsExactly("other-device");
        assertThat(mapper.findFcmTokenByUserId(2L))
                .extracting(FcmTokenResponseDto::getToken).containsExactly("another-user-device");
        assertThat(mapper.findAllTokens()).extracting(FcmTokenResponseDto::getToken)
                .containsExactlyInAnyOrder("logout-device", "other-device", "another-user-device");
    }

    @Test
    @DisplayName("등록되지 않은 토큰의 연결 해제 요청은 다른 기기에 영향을 주지 않는다")
    void ignoresUnbindForUnknownDevice() {
        service.updateFcmTokenForUser(request(1L, "existing-device"));
        List<TokenRow> original = savedTokens();

        service.unbindFcmToken("unknown-device");

        assertThat(savedTokens()).containsExactlyElementsOf(original);
    }

    private FcmTokenRequestDto request(Long userId, String token) {
        return FcmTokenRequestDto.builder().userId(userId).token(token).deviceType("WEB").build();
    }

    private List<TokenRow> savedTokens() {
        // 서비스의 반환값이나 모의 객체 호출이 아니라 DB의 실제 저장값을 확인한다.
        return jdbc.query("SELECT id, user_id, token, device_type FROM fcm_tokens ORDER BY id",
                (rs, rowNum) -> new TokenRow(rs.getLong("id"), rs.getObject("user_id", Long.class),
                        rs.getString("token"), rs.getString("device_type")));
    }

    private record TokenRow(long id, Long userId, String token, String deviceType) { }

    @Configuration(proxyBeanMethods = false)
    @EnableTransactionManagement
    @Import(FcmNotificationService.class)
    static class TestConfig {

        @Bean
        DataSource dataSource(Environment environment) {
            // SQL 로그에 테스트 데이터를 출력하지 않도록 PostgreSQL 드라이버로 직접 접속한다.
            String url = environment.getRequiredProperty("spring.datasource.url")
                    .replace("jdbc:log4jdbc:", "jdbc:");
            return new UnpooledDataSource("org.postgresql.Driver", url,
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
            factory.setTypeAliasesPackage("com.limnj.noticeboardadmin.fcm");
            factory.setMapperLocations(new ClassPathResource("mapper/FcmNotificationMapper.xml"));
            return factory.getObject();
        }

        @Bean
        MapperFactoryBean<FcmNotificationMapper> fcmNotificationMapper(SqlSessionFactory sqlSessionFactory) {
            var factory = new MapperFactoryBean<>(FcmNotificationMapper.class);
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
