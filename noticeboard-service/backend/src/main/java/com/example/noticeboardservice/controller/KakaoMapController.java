package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.dto.kakaoResponseDto;
import com.example.noticeboardservice.service.KakaoMapService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class KakaoMapController {

    private final KakaoMapService kakaoMapServiceImpl;

    @GetMapping("/map")
    @Operation(summary = "회원 주소 검색 API")
    public ResponseEntity<List<kakaoResponseDto.AddressResponseDto>> searchKeyword(@RequestParam(value = "query") String query) {
        kakaoResponseDto response = kakaoMapServiceImpl.searchAddressByKeyword(query);
        return ResponseEntity.ok().body(response.getDocuments());
    }
}
