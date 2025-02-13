package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.MessageDto;

public interface MessageMapper {
    int insertMessage(MessageDto messageDto);
    int deleteMessage(Long messageId);
    MessageDto findMessageByMessageId(Long messageId);
}
