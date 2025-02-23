package com.example.noticeboardservice.dto;

public record MessageResponseDto(
        Long id,
        Long senderId,
        Long receiverId,
        String content,
        String createdAt,
        String isRead
) {
}
