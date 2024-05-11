package com.example.noticeboardservice.dto;

import lombok.Getter;

@Getter
public class NoticeResponseDto {
    Long id;
    String title;
    String content;
    String postDate;
    Long views;
    Long memberId;
}
