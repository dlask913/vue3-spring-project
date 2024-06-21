package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.dto.HeartDto;
import com.example.noticeboardservice.service.HeartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class HeartController {
    private final HeartService heartServiceImpl;

    @GetMapping("/heart")
    @Operation(summary = "하트 조회 API")
    public ResponseEntity<?> getHeartStatus(@RequestParam Long memberId, @RequestParam Long commentId){
        HeartDto heartDto = HeartDto.builder()
                .memberId(memberId)
                .commentId(commentId)
                .build();
        HeartDto response = heartServiceImpl.findHeart(heartDto);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/heart")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "하트 누르기 API")
    public ResponseEntity<?> saveHeart(@RequestBody HeartDto heartDto) {
        int result = heartServiceImpl.saveHeart(heartDto);
        if (result <= 0){
            return ResponseEntity.badRequest().body("하트 저장에 실패하였습니다.");
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/heart/{heartId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "하트 취소 API")
    public ResponseEntity<?> deleteHeart(@PathVariable("heartId") Long heartId) {
        int result = heartServiceImpl.deleteHeart(heartId);
        if (result <= 0){
            return ResponseEntity.badRequest().body("하트 취소에 실패하였습니다.");
        }
        return ResponseEntity.ok().build();
    }
}
