package com.limnj.noticeboardadmin.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogStatisticsController {
    private final LogStatisticsService logStatisticsService;

    @GetMapping("/daily/logs/{from-date}/{to-date}")
    public ResponseEntity<?> getDailyLogs(@PathVariable("from-date") String fromDate, @PathVariable("to-date") String toDate){
        LogStatisticsResponseDto response = logStatisticsService.findDailyLogs(fromDate, toDate);
        return ResponseEntity.ok().body(response);
    }
}
