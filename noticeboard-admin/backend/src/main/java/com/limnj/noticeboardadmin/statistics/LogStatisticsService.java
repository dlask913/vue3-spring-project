package com.limnj.noticeboardadmin.statistics;

import org.springframework.stereotype.Service;

@Service
public interface LogStatisticsService {
    LogStatisticsResponseDto findDailyLogs(String fromDate, String toDate);
}
