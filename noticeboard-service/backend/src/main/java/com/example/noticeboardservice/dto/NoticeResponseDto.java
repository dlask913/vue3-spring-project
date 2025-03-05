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
        Long viewCount,
        PostType postType
) {
    public NoticeResponseDto(Long id, String title, String content, String postDate, String updateDate, Long memberId, String username, String email, Long viewCount) {
        this(id, title, content, postDate, updateDate, memberId, username, email, viewCount, PostType.NOTICE);
    }
}
