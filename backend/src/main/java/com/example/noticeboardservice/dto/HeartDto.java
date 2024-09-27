package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class HeartDto {
    private Long id;
    private Long memberId;
    private Long commentId;
    private int cnt;

    @Builder
    public HeartDto(Long id, Long memberId, Long commentId) {
        this.id = id;
        this.memberId = memberId;
        this.commentId = commentId;
    }

    public void updateCnt(int cnt){
        this.cnt = cnt;
    }
}
