package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductBidDto {
    private int bidPrice;
    private String clientEmail;
    private Long productId;

    @Builder
    public ProductBidDto(int bidPrice, String clientEmail, Long productId) {
        this.bidPrice = bidPrice;
        this.clientEmail = clientEmail;
        this.productId = productId;
    }
}
