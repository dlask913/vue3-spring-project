package com.example.noticeboardservice.dto.product;

public record ProductBidResultDto(
        Long bidPrice,
        String customerEmail,
        String ownerEmail
) {
}
