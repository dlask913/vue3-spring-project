package com.example.noticeboardservice.dto;


public record NoticeResponseDto(
        Long id,
        String title,
        String content,
        String postDate,
        String updateDate,
        Long memberId,
        String username,
        String email,
        Long viewCount
) {
}
