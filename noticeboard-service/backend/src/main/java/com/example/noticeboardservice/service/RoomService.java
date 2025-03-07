package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.RoomDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface RoomService {
    int insertRoom(RoomDto roomDto);
    Optional<RoomDto> findRoomByMembers(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
    List<RoomDto> findRoomsByMemberId(Long memberId);
}
