package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.HeartDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HeartMapper {
    HeartDto findHeart(HeartDto hearDto);
    int saveHeart(HeartDto heartDto);
    int deleteHeart(Long heartId);

}
