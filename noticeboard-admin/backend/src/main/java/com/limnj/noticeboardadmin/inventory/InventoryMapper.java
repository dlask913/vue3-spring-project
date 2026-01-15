package com.limnj.noticeboardadmin.inventory;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface InventoryMapper {
    void saveAll(List<InventoryRequestDto> inventories);
    List<InventoryResponseDto> findAllInventories();
    List<InventoryResponseDto> findInventoryByName(String inventoryName);
}
