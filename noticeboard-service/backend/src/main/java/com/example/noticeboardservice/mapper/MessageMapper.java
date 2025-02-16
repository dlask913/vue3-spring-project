package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.MessageDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insertMessage(MessageDto messageDto);
    int deleteMessage(Long messageId);
    MessageDto findMessageByMessageId(Long messageId);
    List<MessageDto> findAllMessages();
    void deleteAllMessages();
    List<MessageDto> findReceivedMessagesByMemberId(Long memberId);
    List<MessageDto> findSentMessagesByMemberId(Long memberId);
}
