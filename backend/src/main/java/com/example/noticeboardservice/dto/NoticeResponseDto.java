package com.example.noticeboardservice.dto;

import lombok.Getter;

@Getter
public class NoticeResponseDto {
    Long id;
    String title;
    String content;
    String postDate;
    String updateDate;
    Long memberId;
    String username;
    String email;
    Long viewCount;
}
