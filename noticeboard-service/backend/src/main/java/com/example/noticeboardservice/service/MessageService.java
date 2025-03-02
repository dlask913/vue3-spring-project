package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MessageRequestDto;
import com.example.noticeboardservice.dto.MessageResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {
    int sendMessage(MessageRequestDto messageRequestDto);
    int deleteMessage(Long messageId);
    MessageResponseDto findMessageByMessageId(Long messageId);
    List<MessageResponseDto> findReceivedMessagesByMemberId(Long memberId);
    List<MessageResponseDto> findSentMessagesByMemberId(Long memberId);
    int updateReadStatus(Long messageId);
    List<MessageResponseDto> findMessagesByRoomId(Long roomId);
}
