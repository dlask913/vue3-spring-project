package com.example.noticeboardservice.mapper.heart;

import com.example.noticeboardservice.dto.heart.HeartDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HeartMapper {
    List<HeartDto> findHeartByCommentId(HeartDto hearDto);
    HeartDto findHeart(HeartDto hearDto);
    int saveHeart(HeartDto heartDto);
    int deleteHeart(Long heartId);
    List<HeartDto> findAllHearts();
    void deleteAll();
}
