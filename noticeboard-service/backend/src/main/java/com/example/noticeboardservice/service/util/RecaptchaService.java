package com.example.noticeboardservice.service.util;

import com.example.noticeboardservice.dto.member.RecaptchaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class RecaptchaService {

    @Value("${google.recaptcha.secret-key}") // application.yml에 정의된 Secret Key
    private String secretKey;

    private static final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    public boolean verify(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();

        // 파라미터 구성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", token);

        try {
            // Google 서버에 POST 요청
            RecaptchaResponse response = restTemplate.postForObject(
                    GOOGLE_RECAPTCHA_VERIFY_URL,
                    params,
                    RecaptchaResponse.class
            );

            return response != null && response.isSuccess();
        } catch (Exception e) {
            // 네트워크 오류 등 예외 처리
            return false;
        }
    }
}