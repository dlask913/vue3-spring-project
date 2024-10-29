package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class ReplyRequestDto {
    private Long id;
    private String content;
    private Long memberId;
    private Long commentId;

    @Builder
    public ReplyRequestDto(Long id, String content, Long memberId, Long commentId) {
        this.id = id;
        this.content = content;
        this.memberId = memberId;
        this.commentId = commentId;
    }
}
