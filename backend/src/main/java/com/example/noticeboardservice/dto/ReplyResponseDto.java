package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReplyResponseDto {
    Long id;
    String content;
    String username;
    Long memberId;
    Long commentId;
    String postDate;
    String updateDate;
    String memberImgUrl;

    public ReplyResponseDto(Long id, String content, String username, Long memberId, Long commentId, String postDate, String memberImgUrl) {
        this.id = id;
        this.content = content;
        this.username = username;
        this.memberId = memberId;
        this.commentId = commentId;
        this.postDate = postDate;
        this.memberImgUrl = memberImgUrl == null ? "/image/memberDefaultImg.jpg" : memberImgUrl;
    }
}
