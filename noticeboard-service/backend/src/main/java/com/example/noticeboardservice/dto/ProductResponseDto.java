package com.example.noticeboardservice.dto;

public record ProductResponseDto (
        Long id,
        String title,
        String content,
        String category,
        Integer standardPrice,
        String imgUrl,
        Long ownerId,
        String postDate
){
}
