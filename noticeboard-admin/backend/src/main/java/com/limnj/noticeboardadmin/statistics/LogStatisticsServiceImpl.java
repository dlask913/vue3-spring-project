package com.limnj.noticeboardadmin.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @Transactional
@RequiredArgsConstructor
public class LogStatisticsServiceImpl implements LogStatisticsService{

    private final LogStatisticsMapper logStatisticsMapper;

    @Override
    public LogStatisticsResponseDto findDailyLogs(String fromDate, String toDate) {
        LogStatisticsRequestDto requestDto = LogStatisticsRequestDto.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .build();
        return logStatisticsMapper.findDailyLogs(requestDto);
    }
}
