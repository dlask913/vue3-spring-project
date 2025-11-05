package com.example.noticeboardservice.mapper.message;

import com.example.noticeboardservice.dto.message.RoomDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoomMapper {
    int insertRoom(RoomDto roomDto);
    RoomDto findRoomByMembers(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
    List<RoomDto> findRoomsByMemberId(Long memberId);
    void deleteAllRooms();
}
