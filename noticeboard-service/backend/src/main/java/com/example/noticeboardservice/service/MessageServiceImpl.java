package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MessageDto;
import com.example.noticeboardservice.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService{
    private final MessageMapper messageMapper;
    @Override
    public int sendMessage(MessageDto messageDto) {
        return messageMapper.insertMessage(messageDto);
    }

    @Override
    public int deleteMessage(Long messageId) {
        return messageMapper.deleteMessage(messageId);
    }

    @Override
    public MessageDto findMessageByMessageId(Long messageId) {
        return messageMapper.findMessageByMessageId(messageId);
    }
}
