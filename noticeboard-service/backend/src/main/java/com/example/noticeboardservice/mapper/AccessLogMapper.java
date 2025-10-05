package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.AccessLogDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccessLogMapper {
    void insertAccessLog(AccessLogDto accessLogDto);
}
