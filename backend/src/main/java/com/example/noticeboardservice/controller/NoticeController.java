package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.dto.NoticeRequestDto;
import com.example.noticeboardservice.dto.NoticeResponseDto;
import com.example.noticeboardservice.service.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeServiceImpl;

    @GetMapping("/notice/{noticeId}")
    @Operation(summary = "게시글 단일 조회 API")
    public ResponseEntity<?> findNotice(@PathVariable("noticeId") Long noticeId) {
        NoticeResponseDto response = noticeServiceImpl.findNotice(noticeId);
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/notice")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "게시글 저장 API")
    public ResponseEntity<?> saveNotice(@RequestBody NoticeRequestDto noticeRequestDto) {
        int result = noticeServiceImpl.saveNotice(noticeRequestDto);
        if (result <= 0) {
            return ResponseEntity.badRequest().body("게시글 저장에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("게시글 저장이 완료되었습니다.");
    }

     @PutMapping("/notice/{noticeId}")
     @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "게시글 수정 API")
     public ResponseEntity<?> updateNotice(@PathVariable("noticeId") Long noticeId,
                                           @RequestBody NoticeRequestDto noticeRequestDto) {
         int result = noticeServiceImpl.updateNotice(noticeId, noticeRequestDto);
         if (result <= 0) {
             return ResponseEntity.badRequest().body("게시글 수정에 실패하였습니다.");
         }
         return ResponseEntity.ok().body("수정이 완료되었습니다.");
     }

    @DeleteMapping("/notice/{noticeId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "게시글 삭제 API")
    public ResponseEntity<String> deleteNotice(@PathVariable("noticeId") Long noticeId) {
        int result = noticeServiceImpl.deleteNotice(noticeId);
        if (result <= 0){
            return ResponseEntity.badRequest().body("게시글 삭제에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("게시글 삭제가 완료되었습니다.");
    }

    @GetMapping("/notices/all")
    @Operation(summary = "모든 게시글 조회 API")
    public ResponseEntity<List<NoticeResponseDto>> getAllNotices() {
        List<NoticeResponseDto> notices = noticeServiceImpl.findAllNotices();
        return ResponseEntity.ok().body(notices);
    }

    @GetMapping("/notices")
    @Operation(summary = "게시글 검색 AND 페이지네이션 API")
    public ResponseEntity<List<NoticeResponseDto>> searchNoticesByPage(
            @RequestParam(value = "page", defaultValue = "1") String page,
            @RequestParam(value = "limit", defaultValue = "5") String limit,
            @RequestParam Map<String, String> params
    ) {
        List<NoticeResponseDto> notices = noticeServiceImpl.searchNoticeByPage(
                Integer.parseInt(page), Integer.parseInt(limit), params);
        return ResponseEntity.ok().body(notices);
    }
}
