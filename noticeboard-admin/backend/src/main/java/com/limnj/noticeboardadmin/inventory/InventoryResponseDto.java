package com.limnj.noticeboardadmin.inventory;

import lombok.Getter;

@Getter
public class InventoryResponseDto {
    String productCode;
    String productName;
    Long orderQty;
    int unitPrice;
    String orderDate;
}
