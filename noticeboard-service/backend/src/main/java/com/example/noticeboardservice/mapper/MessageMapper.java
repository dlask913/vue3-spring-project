package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.MessageRequestDto;
import com.example.noticeboardservice.dto.MessageResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper {
    int insertMessage(MessageRequestDto messageRequestDto);
    int deleteMessage(Long messageId);
    MessageResponseDto findMessageById(Long messageId);
    List<MessageResponseDto> findAllMessages();
    void deleteAllMessages();
    List<MessageResponseDto> findReceivedMessagesByMemberId(Long memberId);
    List<MessageResponseDto> findSentMessagesByMemberId(Long memberId);
    int updateReadStatus(@Param("userId") Long userId, @Param("otherId") Long otherId);
    List<MessageResponseDto> findMessagesByRoomId(Long roomId);
    MessageResponseDto findLatestMessageByRoomId(Long roomId);
}
