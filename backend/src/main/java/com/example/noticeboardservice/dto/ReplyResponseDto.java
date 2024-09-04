package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ReplyResponseDto {
    Long id;
    String content;
    Long memberId;
    Long commentId;
    String postDate;
    String updateDate;
}
