package com.limnj.noticeboardadmin.statistics;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LogStatisticsResponseDto {
    String uri;
    String method;
    LocalDate logDate;
    long count;
}
