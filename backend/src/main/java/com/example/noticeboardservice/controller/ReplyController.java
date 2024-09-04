package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.dto.CommentRequestDto;
import com.example.noticeboardservice.dto.CommentResponseDto;
import com.example.noticeboardservice.dto.ReplyRequestDto;
import com.example.noticeboardservice.dto.ReplyResponseDto;
import com.example.noticeboardservice.service.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyServiceImpl;

    @PostMapping("/reply")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "대댓글 저장 API")
    private ResponseEntity<String> saveReply(@RequestBody ReplyRequestDto replyDto) {
        int result = replyServiceImpl.insertReply(replyDto);
        if (result <= 0){
            return ResponseEntity.badRequest().body("대댓글 등록에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("대댓글을 저장하였습니다.");
    }

    @GetMapping("/replies/{commentId}")
    @Operation(summary = "특정 댓글의 대댓글 조회 API")
    private ResponseEntity<List<ReplyResponseDto>> findRepliesByCommentId(@PathVariable("commentId") Long commentId) {
        List<ReplyResponseDto> replies = replyServiceImpl.findRepliesByCommentId(commentId);
        return ResponseEntity.ok().body(replies);
    }

    @PutMapping("/reply/{replyId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "대댓글 수정 API")
    private ResponseEntity<String> updateReply(@RequestBody ReplyRequestDto replyDto) {
        int result = replyServiceImpl.updateReply(replyDto);
        if (result <= 0){
            return ResponseEntity.badRequest().body("수정 실패하였습니다.");
        }
        return ResponseEntity.ok().body("수정 완료되었습니다.");
    }

    @DeleteMapping("/reply/{replyId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "대댓글 삭제 API")
    private ResponseEntity<String> deleteReply(@PathVariable("replyId") Long replyId) {
        int result = replyServiceImpl.deleteReply(replyId);
        if (result <= 0){
            return ResponseEntity.badRequest().body("삭제 실패하였습니다.");
        }
        return ResponseEntity.ok().body("삭제 완료되었습니다.");
    }

}
