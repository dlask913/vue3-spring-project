package com.example.noticeboardservice.dto;


public record CommentResponseDto(
        Long id,
        String content,
        String postDate,
        String username,
        String memberImgUrl,
        Long memberId,
        Long noticeId,
        Long replyCount
) {
    public CommentResponseDto updatedReplyCount(Long replyCount) {
        return new CommentResponseDto(id, content, postDate, username, memberImgUrl, memberId, noticeId, replyCount);
    }
}
