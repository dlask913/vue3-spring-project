package com.limnj.noticeboardadmin.notice;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeServiceImpl;

    @PostMapping("/notice")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") },summary = "관리자용 게시글 저장 API")
    public ResponseEntity<?> saveNotice(@RequestPart(value = "noticeRequestDto") NoticeRequestDto noticeRequestDto,
                                        @RequestPart(value = "noticeFile", required = false) MultipartFile noticeFile) {
        int result = noticeServiceImpl.saveNotice(noticeRequestDto, noticeFile);
        if (result <= 0) {
            return ResponseEntity.badRequest().body("게시글 저장에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("게시글 저장이 완료되었습니다.");
    }

    @PutMapping("/notice")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") },summary = "관리자용 게시글 수정 API")
    public ResponseEntity<?> updateNotice(@RequestBody NoticeRequestDto noticeRequestDto) {
        int result = noticeServiceImpl.updateNotice(noticeRequestDto);
        if (result <= 0) {
            return ResponseEntity.badRequest().body("게시글 수정에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("수정이 완료되었습니다.");
    }

    @DeleteMapping("/notice/{noticeId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") },summary = "관리자용 게시글 삭제 API")
    public ResponseEntity<String> deleteNotice(@PathVariable("noticeId") Long noticeId) {
        int result = noticeServiceImpl.deleteNotice(noticeId);
        if (result <= 0){
            return ResponseEntity.badRequest().body("게시글 삭제에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("게시글 삭제가 완료되었습니다.");
    }

    @GetMapping("/notice/{noticeId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") },summary = "게시글 단일 조회 API")
    public ResponseEntity<?> findNotice(@PathVariable("noticeId") Long noticeId) {
        NoticeResponseDto response = noticeServiceImpl.findNoticeByNoticeId(noticeId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/notices")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") },summary = "게시글 모두 조회 API")
    public ResponseEntity<List<NoticeResponseDto>> findAllNotices(){
        List<NoticeResponseDto> response = noticeServiceImpl.findAllNotices();
        return ResponseEntity.ok().body(response);
    }
}
