package com.limnj.noticeboardadmin.statistics;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LogStatisticsMapper {
    LogStatisticsResponseDto findDailyLogs(LogStatisticsRequestDto requestDto);
}
