package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentRequestDto {
    private Long id;
    private String content;
    private LocalDateTime postDate;
    private Long memberId;
    private Long noticeId;

    @Builder
    public CommentRequestDto(Long id, String content, Long memberId, Long noticeId) {
        this.id = id;
        this.content = content;
        this.postDate = LocalDateTime.now();
        this.memberId = memberId;
        this.noticeId = noticeId;
    }
}
