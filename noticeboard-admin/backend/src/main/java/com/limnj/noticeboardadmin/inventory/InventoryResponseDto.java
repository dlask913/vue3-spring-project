package com.limnj.noticeboardadmin.inventory;

import lombok.Getter;

@Getter
public class InventoryResponseDto {
    Long productId;
    String productCode;
    String productName;
    Long orderQty;
    int unitPrice;
    String orderDate;
}
