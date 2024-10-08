package com.example.noticeboardservice.dto;

public record ProductResponseDto (
        Long id,
        String title,
        String content,
        int standardPrice,
        int auctionPrice,
        String customerEmail,
        Long ownerId
){
}
