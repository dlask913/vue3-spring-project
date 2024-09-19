package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String postDate;
    private String username;
    private String memberImgUrl;
    private Long memberId;
    private Long noticeId;

    @Builder
    public CommentResponseDto(Long id, String content, String postDate, String username, String memberImgUrl, Long memberId, Long noticeId) {
        this.id = id;
        this.content = content;
        this.postDate = postDate;
        this.username = username;
        this.memberImgUrl = memberImgUrl == null ? "/image/memberDefaultImg.jpg" : memberImgUrl;
        this.memberId = memberId;
        this.noticeId = noticeId;
    }
}
