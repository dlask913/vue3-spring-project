package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MessageDto;
import org.springframework.stereotype.Service;

@Service
public interface MessageService {
    int sendMessage(MessageDto messageDto);
    int deleteMessage(Long messageId);
    MessageDto findMessageByMessageId(Long messageId);
}
