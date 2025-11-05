package com.example.noticeboardservice.dto.message;

public record MessageResponseDto(
        Long id,
        Long senderId,
        Long receiverId,
        String content,
        String createdAt,
        String isRead,
        Long roomId
) {
}
