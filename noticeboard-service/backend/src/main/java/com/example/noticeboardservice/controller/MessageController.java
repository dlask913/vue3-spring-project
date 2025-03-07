package com.example.noticeboardservice.controller;

import com.example.noticeboardservice.dto.MessageRequestDto;
import com.example.noticeboardservice.dto.MessageResponseDto;
import com.example.noticeboardservice.dto.RoomDto;
import com.example.noticeboardservice.service.MessageService;
import com.example.noticeboardservice.service.RoomService;
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
    private final RoomService roomServiceImpl;

    @PostMapping("/message")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "메시지 저장 API")
    private ResponseEntity<String> saveMessage(@RequestBody MessageRequestDto messageRequestDto) {
        int result = messageServiceImpl.sendMessage(messageRequestDto);
        if (result <= 0){
            return ResponseEntity.badRequest().body("메시지 전송에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("메시지를 전송하였습니다.");
    }

    @GetMapping("/message/{messageId}")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "메시지 상세 조회 API")
    public ResponseEntity<MessageResponseDto> findMessageByMessageId(@PathVariable("messageId") Long messageId) {
        MessageResponseDto response = messageServiceImpl.findMessageByMessageId(messageId);
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
    public ResponseEntity<List<MessageResponseDto>> findReceivedMessagesByMemberId(@PathVariable("memberId") Long memberId){
        List<MessageResponseDto> messages = messageServiceImpl.findReceivedMessagesByMemberId(memberId);
        return ResponseEntity.ok().body(messages); 
    }

    @GetMapping("/member/{memberId}/messages/sent")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "보낸 메시지 모두 조회 API")
    public ResponseEntity<List<MessageResponseDto>> findSentMessagesByMemberId(@PathVariable("memberId") Long memberId){
        List<MessageResponseDto> messages = messageServiceImpl.findSentMessagesByMemberId(memberId);
        return ResponseEntity.ok().body(messages);
    }

    @PatchMapping("/message/{messageId}/read")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "메시지 읽음 상태 저장을 위한 API")
    public ResponseEntity<String> updateReadStatusOfMessage(@PathVariable("messageId") Long messageId){
        int result = messageServiceImpl.updateReadStatus(messageId);
        if (result <= 0){
            return ResponseEntity.badRequest().body("메시지 읽음 상태 저장에 실패하였습니다.");
        }
        return ResponseEntity.ok().body("메시지 읽음 처리가 완료되었습니다.");
    }

    @GetMapping("/room/{roomId}/messages")
    @Operation(security =  { @SecurityRequirement(name = "bearerAuth") }, summary = "특정 채팅방의 메시지 전체 조회 API")
    public ResponseEntity<List<MessageResponseDto>> findMessagesByRoomId(@PathVariable("roomId") Long roomId) {
        List<MessageResponseDto> messages = messageServiceImpl.findMessagesByRoomId(roomId);
        return ResponseEntity.ok().body(messages);
    }

    @GetMapping("/rooms/{memberId}")
    @Operation(security = {@SecurityRequirement(name = "bearerAuth")}, summary = "회원이 속한 모든 채팅방 조회 API")
    public ResponseEntity<List<RoomDto>> findRoomsByMemberId(@PathVariable("memberId") Long memberId) {
        List<RoomDto> rooms = roomServiceImpl.findRoomsByMemberId(memberId);
        return ResponseEntity.ok().body(rooms);
    }
}
