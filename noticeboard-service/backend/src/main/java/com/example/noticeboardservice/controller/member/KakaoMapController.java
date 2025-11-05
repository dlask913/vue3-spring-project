package com.example.noticeboardservice.controller.member;

import com.example.noticeboardservice.dto.member.kakaoResponseDto;
import com.example.noticeboardservice.service.member.KakaoMapService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class KakaoMapController {

    private final KakaoMapService kakaoMapServiceImpl;

    @GetMapping("/map")
    @Operation(summary = "회원 주소 검색 API")
    public ResponseEntity<kakaoResponseDto> searchKeyword(
            @RequestParam(value = "query") String query,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        kakaoResponseDto response = kakaoMapServiceImpl.searchAddressByKeyword(query, page, limit);
        return ResponseEntity.ok().body(response);
    }
}
