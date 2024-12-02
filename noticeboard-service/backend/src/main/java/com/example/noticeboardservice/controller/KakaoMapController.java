package com.example.noticeboardservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequiredArgsConstructor
public class KakaoMapController {

    private final RestTemplate restTemplate;

    @Value("${kakao-rest-api-key}")
    private String REST_API_KEY;

    @GetMapping("/search")
    public ResponseEntity<String> searchKeyword(@RequestParam String query) {
        String url = "https://dapi.kakao.com/v2/local/search/keyword.json";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + REST_API_KEY);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                url + "?query=" + query,
                HttpMethod.GET,
                entity,
                String.class
        );
    }
}
