package com.limnj.noticeboardadmin.statistics;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class LogStatisticsRequestDto {
    String fromDate;
    String toDate;

    @Builder
    public LogStatisticsRequestDto(String fromDate, String toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }
}
