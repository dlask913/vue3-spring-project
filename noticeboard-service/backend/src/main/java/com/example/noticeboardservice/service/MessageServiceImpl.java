package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MessageRequestDto;
import com.example.noticeboardservice.dto.MessageResponseDto;
import com.example.noticeboardservice.dto.RoomDto;
import com.example.noticeboardservice.mapper.MessageMapper;
import com.example.noticeboardservice.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageServiceImpl implements MessageService{
    private final MessageMapper messageMapper;
    private final RoomService roomServiceImpl;
    @Override
    public int sendMessage(MessageRequestDto messageRequestDto) {
        Optional<RoomDto> findRoom = roomServiceImpl.findRoomByMembers(messageRequestDto.getSenderId(), messageRequestDto.getReceiverId());
        if (findRoom.isEmpty()) { // 멤버들 간 채팅방이 없을 때 생성
            RoomDto roomDto = RoomDto.builder()
                    .senderId(messageRequestDto.getSenderId())
                    .receiverId(messageRequestDto.getReceiverId())
                    .build();
            roomServiceImpl.insertRoom(roomDto);
            messageRequestDto.saveRoomId(roomDto.getId());
        } else {
            messageRequestDto.saveRoomId(findRoom.get().getId());
        }
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
