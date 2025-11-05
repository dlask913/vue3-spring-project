package com.example.noticeboardservice.dto.product;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductBidDto {
    private Long id;
    @NotNull(message = "bidPrice 은 필수 값 입니다.")
    private int bidPrice;
    private Long customerId;
    private String customerEmail;
    private Long productId;

    @Builder
    public ProductBidDto(Long id, int bidPrice, Long customerId, String customerEmail, Long productId) {
        this.id = id;
        this.bidPrice = bidPrice;
        this.customerId = customerId;
        this.customerEmail = customerEmail;
        this.productId = productId;
    }
}
