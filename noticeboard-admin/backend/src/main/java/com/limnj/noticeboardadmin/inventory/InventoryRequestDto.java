package com.limnj.noticeboardadmin.inventory;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InventoryRequestDto {
    String productCode;
    String productName;
    Long orderQty;
    int unitPrice;
    String orderDate;

    @Builder
    public InventoryRequestDto(String productCode, String productName, Long orderQty, int unitPrice, String orderDate) {
        this.productCode = productCode;
        this.productName = productName;
        this.orderQty = orderQty;
        this.unitPrice = unitPrice;
        this.orderDate = orderDate;
    }
}
