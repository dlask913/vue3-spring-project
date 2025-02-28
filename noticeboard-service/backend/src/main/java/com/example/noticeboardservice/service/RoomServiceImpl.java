package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.RoomDto;
import com.example.noticeboardservice.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service @Transactional @RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{
    private final RoomMapper roomMapper;
    @Override
    public int insertRoom(RoomDto roomDto) {
        return roomMapper.insertRoom(roomDto);
    }

    @Override
    public Optional<RoomDto> findRoomByMembers(Long senderId, Long receiverId) {
        RoomDto roomDto = roomMapper.findRoomByMembers(senderId, receiverId);
        return Optional.ofNullable(roomDto);
    }
}
