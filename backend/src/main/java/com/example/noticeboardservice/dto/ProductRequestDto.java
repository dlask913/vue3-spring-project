package com.example.noticeboardservice.dto;

import lombok.Getter;

@Getter
public class ProductRequestDto {
    private Long id;
    private String title;
    private String content;
    private int standardPrice; // 불변
    private int auctionPrice;
    private String customerEmail;
    private Long ownerId;
}