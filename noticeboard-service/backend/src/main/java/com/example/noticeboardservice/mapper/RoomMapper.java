package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.RoomDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RoomMapper {
    int insertRoom(RoomDto roomDto);
    RoomDto findRoomByMembers(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
}
