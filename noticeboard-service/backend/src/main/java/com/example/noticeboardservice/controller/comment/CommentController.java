package com.example.noticeboardservice.controller.comment;

import com.example.noticeboardservice.dto.comment.CommentRequestDto;
import com.example.noticeboardservice.dto.comment.CommentResponseDto;
import com.example.noticeboardservice.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentServiceImpl;

    @PostMapping("/comment")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "댓글 저장 API")
    private ResponseEntity<String> saveComment(@RequestBody CommentRequestDto commentDto) {
        int result = commentServiceImpl.insertComment(commentDto);
        if (result <= 0){
            return ResponseEntity.badRequest().body("댓글 등록에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("댓글을 저장하였습니다.");
    }

    @GetMapping("/comment/{noticeId}")
    @Operation(summary = "특정 게시글 내 댓글 조회 API")
    public ResponseEntity<List<CommentResponseDto>> findCommentsByNoticeId(@PathVariable("noticeId") Long noticeId) {
        List<CommentResponseDto> comments = commentServiceImpl.findCommentsByNoticeId(noticeId);
        return ResponseEntity.ok().body(comments);
    }

    @PutMapping("/comment/{commentId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "댓글 수정 API")
    public ResponseEntity<String> updateComment(@RequestBody CommentRequestDto commentDto) {
        int result = commentServiceImpl.updateComment(commentDto);
        if (result <= 0){
            return ResponseEntity.badRequest().body("수정 실패하였습니다.");
        }
        return ResponseEntity.ok().body("수정 완료되었습니다.");
    }

    @DeleteMapping("/comment/{commentId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "댓글 삭제 API")
    public ResponseEntity<String> deleteComment(@PathVariable("commentId") Long commentId) {
        int result = commentServiceImpl.deleteComment(commentId);
        if (result <= 0){
            return ResponseEntity.badRequest().body("삭제 실패하였습니다.");
        }
        return ResponseEntity.ok().body("삭제 완료되었습니다.");
    }

    @GetMapping("/member/comments")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "내가 쓴 댓글 조회 API")
    public ResponseEntity<List<CommentResponseDto>> findCommentsByUser(Authentication authentication) {
        List<CommentResponseDto> comments = commentServiceImpl.findCommentsByUser(authentication.getName());
        return ResponseEntity.ok().body(comments);
    }
}
