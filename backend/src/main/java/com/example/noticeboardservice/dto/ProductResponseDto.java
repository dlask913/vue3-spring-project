package com.example.noticeboardservice.dto;

public record ProductResponseDto (
        Long id,
        String title,
        String content,
        Integer standardPrice,
        Long ownerId
){
}
