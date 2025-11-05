package com.example.noticeboardservice.mapper.common;

import com.example.noticeboardservice.dto.common.AccessLogDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccessLogMapper {
    void insertAccessLog(AccessLogDto accessLogDto);
}
