package com.example.noticeboardservice.dto.product;

public record ProductDetailsResponseDto(
        Long id,
        String title,
        String content,
        String category,
        Integer standardPrice,
        Integer latestPrice,
        String imgUrl,
        Long ownerId,
        Long customerId,
        String postDate,
        String deadline
){
}