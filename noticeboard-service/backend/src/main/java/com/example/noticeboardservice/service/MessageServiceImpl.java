package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MessageRequestDto;
import com.example.noticeboardservice.dto.MessageResponseDto;
import com.example.noticeboardservice.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService{
    private final MessageMapper messageMapper;
    @Override
    public int sendMessage(MessageRequestDto messageRequestDto) {
        return messageMapper.insertMessage(messageRequestDto);
    }

    @Override
    public int deleteMessage(Long messageId) {
        return messageMapper.deleteMessage(messageId);
    }

    @Override
    public MessageResponseDto findMessageByMessageId(Long messageId) {
        return messageMapper.findMessageByMessageId(messageId);
    }

    @Override
    public List<MessageResponseDto> findReceivedMessagesByMemberId(Long memberId) {
        return messageMapper.findReceivedMessagesByMemberId(memberId);
    }

    @Override
    public List<MessageResponseDto> findSentMessagesByMemberId(Long memberId) {
        return messageMapper.findSentMessagesByMemberId(memberId);
    }

    @Override
    public int updateReadStatus(Long messageId) {
        return messageMapper.updateReadStatus(messageId);
    }
}
