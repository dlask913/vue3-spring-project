package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.MessageRequestDto;
import com.example.noticeboardservice.dto.MessageResponseDto;
import com.example.noticeboardservice.dto.RoomDto;
import com.example.noticeboardservice.mapper.MessageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static java.lang.Math.max;
import static java.lang.Math.min;

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
                    .lowerId(min(messageRequestDto.getSenderId(), messageRequestDto.getReceiverId()))
                    .higherId(max(messageRequestDto.getSenderId(), messageRequestDto.getReceiverId()))
                    .build();
            int result = roomServiceImpl.insertRoom(roomDto);
            if (result <= 0) { // todo: 예외처리
                throw new RuntimeException("채팅방 생성에 실패하였습니다.");
            }
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
        return messageMapper.findMessageById(messageId);
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

    @Override
    public List<MessageResponseDto> findMessagesByRoomId(Long roomId) {
        return messageMapper.findMessagesByRoomId(roomId);
    }
}
