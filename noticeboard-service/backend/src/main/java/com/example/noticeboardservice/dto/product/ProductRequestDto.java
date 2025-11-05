package com.example.noticeboardservice.dto.product;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRequestDto {
    private Long id;
    private String title;
    private String content;
    private String category;
    private int standardPrice; // 불변
    private Long ownerId;
    private String deadline;

    @Builder
    public ProductRequestDto(Long id, String title, String content, String category, int standardPrice, Long ownerId, String deadline) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.category = category;
        this.standardPrice = standardPrice;
        this.ownerId = ownerId;
        this.deadline = deadline;
    }
}