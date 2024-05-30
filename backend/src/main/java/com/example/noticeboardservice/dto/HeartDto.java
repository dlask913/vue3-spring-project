package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class HeartDto {
    private Long id;
    private Long memberId;
    private Long commentId;

    @Builder
    public HeartDto(Long id, Long memberId, Long commentId) {
        this.id = id;
        this.memberId = memberId;
        this.commentId = commentId;
    }
}
