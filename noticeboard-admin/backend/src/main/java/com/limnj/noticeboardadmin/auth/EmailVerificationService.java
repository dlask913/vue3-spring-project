package com.limnj.noticeboardadmin.auth;


import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public interface EmailVerificationService {
    boolean sendVerificationCode(String email);
    boolean verifyCode(String email, String code);

    // 모든 구현체에서 공통으로 사용할 기본 메서드
    default String loadHtmlTemplate(String path) {
        try (var inputStream = new ClassPathResource(path).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("이메일 템플릿 로드 실패: " + path, e);
        }
    }
}
