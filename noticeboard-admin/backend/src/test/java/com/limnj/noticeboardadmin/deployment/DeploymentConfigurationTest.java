package com.limnj.noticeboardadmin.deployment;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DeploymentConfigurationTest {
    @TempDir
    Path directory;

    @Test
    void productionUsesExternalSecretsAndCloudFrontApiPath() throws Exception {
        Path secrets = directory.resolve("secrets.properties");
        Files.writeString(secrets, """
                POSTGRES_HOST=db.example.test
                POSTGRES_USER=app-test-user
                POSTGRES_PASSWORD=test-only-password
                MAIL_USERNAME=test@example.test
                MAIL_PASSWORD=test-only-mail-password
                JWT_SECRET=test-only-jwt-signing-key-32-bytes
                ADMIN_FRONTEND_ORIGIN=https://admin.example.test
                """);
        try (var context = load(secrets, Map.of())) {
            var env = context.getEnvironment();
            assertThat(env.getRequiredProperty("server.servlet.context-path")).isEqualTo("/api");
            assertThat(env.getRequiredProperty("spring.datasource.url"))
                    .startsWith("jdbc:postgresql://db.example.test:5432/noticeboard?")
                    .contains("sslmode=verify-full", "sslrootcert=/var/lib/noticeboard-admin/config/");
            assertThat(env.getRequiredProperty("spring.datasource.username")).isEqualTo("app-test-user");
            assertThat(env.getRequiredProperty("spring.datasource.password")).isEqualTo("test-only-password");
            assertThat(env.getRequiredProperty("app.cors.allowed-origins")).isEqualTo("https://admin.example.test");
            assertThat(env.getRequiredProperty("httponly-secure-option", Boolean.class)).isTrue();
            assertThat(env.getRequiredProperty("firebase.credentials"))
                    .isEqualTo("file:/var/lib/noticeboard-admin/config/firebase-service-account.json");
            assertThat(env.getRequiredProperty("fileLocation")).startsWith("/var/lib/noticeboard-admin/");
            assertThat(env.getRequiredProperty("management.endpoint.health.group.readiness.include"))
                    .isEqualTo("readinessState,db");
            assertThat(env.getRequiredProperty("management.endpoints.web.exposure.include")).isEqualTo("health");
        }
        try (var context = load(secrets, Map.of("POSTGRES_PASSWORD", "overridden-test-password"))) {
            assertThat(context.getEnvironment().getRequiredProperty("spring.datasource.password"))
                    .isEqualTo("overridden-test-password");
        }
    }

    @Test
    void productionFailsWhenRequiredServerSecretsAreMissing() {
        assertThatThrownBy(() -> load(directory.resolve("missing.properties"), Map.of()))
                .isInstanceOf(RuntimeException.class);
    }

    private ConfigurableApplicationContext load(Path secrets, Map<String, Object> overrides) {
        var application = new SpringApplication(ConfigOnly.class);
        application.setWebApplicationType(WebApplicationType.NONE);
        application.setBannerMode(Banner.Mode.OFF);
        application.setLogStartupInfo(false);
        var environment = new StandardEnvironment();
        environment.getPropertySources().replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                new MapPropertySource(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, overrides));
        application.setEnvironment(environment);
        return application.run("--spring.profiles.active=prod",
                "--spring.config.location=classpath:/application.yml",
                "--LOCAL_SECRETS_FILE=" + directory.resolve("unused.env").toString().replace('\\', '/'),
                "--ADMIN_SECRETS_FILE=" + secrets.toString().replace('\\', '/'));
    }

    @Configuration(proxyBeanMethods = false)
    static class ConfigOnly { }
}
