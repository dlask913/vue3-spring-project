package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.dto.MessageDto;
import com.example.noticeboardservice.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageServiceImpl;

    @PostMapping("/message")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "메시지 저장 API")
    private ResponseEntity<String> saveMessage(@RequestBody MessageDto messageDto) {
        int result = messageServiceImpl.sendMessage(messageDto);
        if (result <= 0){
            return ResponseEntity.badRequest().body("메시지 전송에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("메시지를 전송하였습니다.");
    }

    @GetMapping("/message/{messageId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "메시지 상세 조회 API")
    public ResponseEntity<MessageDto> findMessageByMessageId(@PathVariable("messageId") Long messageId) {
        MessageDto response = messageServiceImpl.findMessageByMessageId(messageId);
        return ResponseEntity.ok().body(response);
    }

    @DeleteMapping("/message/{messageId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "메시지 삭제 API")
    public ResponseEntity<String> deleteMessage(@PathVariable("messageId") Long messageId) {
        int result = messageServiceImpl.deleteMessage(messageId);
        if (result <= 0){
            return ResponseEntity.badRequest().body("메시지 삭제 실패하였습니다.");
        }
        return ResponseEntity.ok().body("메시지 삭제 완료되었습니다.");
    }

    @GetMapping("/member/{memberId}/messages/received")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "받은 메시지 모두 조회 API")
    public ResponseEntity<List<MessageDto>> findReceivedMessagesByMemberId(@PathVariable("memberId") Long memberId){
        List<MessageDto> messages = messageServiceImpl.findReceivedMessagesByMemberId(memberId);
        return ResponseEntity.ok().body(messages); 
    }

    @GetMapping("/member/{memberId}/messages/sent")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "보낸 메시지 모두 조회 API")
    public ResponseEntity<List<MessageDto>> findSentMessagesByMemberId(@PathVariable("memberId") Long memberId){
        List<MessageDto> messages = messageServiceImpl.findSentMessagesByMemberId(memberId);
        return ResponseEntity.ok().body(messages);
    }
}
