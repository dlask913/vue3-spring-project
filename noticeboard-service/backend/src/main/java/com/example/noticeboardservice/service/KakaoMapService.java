package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.kakaoResponseDto;
import org.springframework.stereotype.Service;


@Service
public interface KakaoMapService {
    /**
     * 카카오 Map API 호출하여 결과 반환
     *
     * @param keyword 검색어
     * @return kakaoResponseDto
     */
    kakaoResponseDto searchAddressByKeyword(String keyword, int page, int size);
}
