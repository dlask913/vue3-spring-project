package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductBidDto {
    private int bidPrice;
    private Long customerId;
    private Long productId;

    @Builder
    public ProductBidDto(int bidPrice, Long customerId, Long productId) {
        this.bidPrice = bidPrice;
        this.customerId = customerId;
        this.productId = productId;
    }
}
