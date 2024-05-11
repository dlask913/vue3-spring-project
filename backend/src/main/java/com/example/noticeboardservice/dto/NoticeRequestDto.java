package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeRequestDto {
    Long id;
    String title;
    String content;
    LocalDateTime postDate;
    Long memberId;

    @Builder
    public NoticeRequestDto(Long id, String title, String content, Long memberId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.postDate = LocalDateTime.now();
        this.memberId = memberId;
    }
}
