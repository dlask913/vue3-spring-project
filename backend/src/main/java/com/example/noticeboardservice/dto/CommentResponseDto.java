package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
public class CommentResponseDto {
    private Long id;
    private String content;
    private String postDate;
    private String username;
    private String memberImgUrl= "/image/memberDefaultImg.jpg";
    private Long memberId;
    private Long noticeId;
}
