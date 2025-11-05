package com.example.noticeboardservice.dto.comment;


public record ReplyResponseDto(
        Long id,
        String content,
        String username,
        Long memberId,
        Long commentId,
        String postDate,
        String updateDate,
        String memberImgUrl
) {
}
