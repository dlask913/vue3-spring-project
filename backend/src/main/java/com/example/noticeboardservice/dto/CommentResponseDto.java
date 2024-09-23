package com.example.noticeboardservice.dto;


public record CommentResponseDto(
        Long id,
        String content,
        String postDate,
        String username,
        String memberImgUrl,
        Long memberId,
        Long noticeId
) {
}
