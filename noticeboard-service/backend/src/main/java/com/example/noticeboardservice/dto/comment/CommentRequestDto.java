package com.example.noticeboardservice.dto.comment;

import lombok.Builder;
import lombok.Getter;


@Getter
public class CommentRequestDto {
    private Long id;
    private String content;
    private Long memberId;
    private Long noticeId;

    @Builder
    public CommentRequestDto(Long id, String content, Long memberId, Long noticeId) {
        this.id = id;
        this.content = content;
        this.memberId = memberId;
        this.noticeId = noticeId;
    }
}
