package com.example.noticeboardservice.service.member;

import com.example.noticeboardservice.dto.member.kakaoResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;



@Service
@Transactional
@RequiredArgsConstructor
public class KakaoMapServiceImpl implements KakaoMapService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${kakao-rest-api-key}")
    private String REST_API_KEY;

    @Override
    public kakaoResponseDto searchAddressByKeyword(String keyword, int page, int limit) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + REST_API_KEY);
        HttpEntity<?> entity = new HttpEntity<>(headers);

        // String.format() 으로 하면 한글 깨짐 → 인코딩한 경우 검색 결과 조회 불가 → 문자열 직접 연결
        String requestUrl = "https://dapi.kakao.com/v2/local/search/keyword.json?query="
                + keyword + "&page=" + page + "&size=" + limit;

        ResponseEntity<String> response = restTemplate.exchange(
                requestUrl,
                HttpMethod.GET,
                entity,
                String.class
        );

        kakaoResponseDto kakaoResponseDto;
        try {
            kakaoResponseDto = objectMapper.readValue(response.getBody(), kakaoResponseDto.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            int totalCount = rootNode.path("meta").path("total_count").asInt();
            kakaoResponseDto.saveTotalCount(Math.min(45, totalCount)); // pageable_count maximum 값 제한
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return kakaoResponseDto;
    }
}
