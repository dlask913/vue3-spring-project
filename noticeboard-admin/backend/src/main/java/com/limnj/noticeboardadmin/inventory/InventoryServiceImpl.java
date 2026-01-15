package com.limnj.noticeboardadmin.inventory;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service @Transactional
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryMapper inventoryMapper;

    @Override
    public void uploadInventoryBulk(MultipartFile multipartFile) {
        List<InventoryRequestDto> inventories = validateInventoryBulk(multipartFile);
        inventoryMapper.saveAll(inventories);
    }

    @Override
    public List<InventoryRequestDto> validateInventoryBulk(MultipartFile multipartFile) {
        DataFormatter formatter = new DataFormatter();
        List<InventoryRequestDto> inventories = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 헤더 제외
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String productCode = formatter.formatCellValue(row.getCell(0));
                String productName = formatter.formatCellValue(row.getCell(1));
                String orderQtyStr = formatter.formatCellValue(row.getCell(2));
                String unitPriceStr = formatter.formatCellValue(row.getCell(3));
                String orderDate = formatter.formatCellValue(row.getCell(4));

                if (productCode.isBlank()) continue; // 필수값 체크 예시

                inventories.add(InventoryRequestDto.builder()
                        .productCode(productCode)
                        .productName(productName)
                        .orderQty(Long.parseLong(orderQtyStr.replace(",", "")))
                        .unitPrice(Integer.parseInt(unitPriceStr.replace(",", "")))
                        .orderDate(orderDate)
                        .build());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return inventories;
    }

    @Override
    public List<InventoryResponseDto> findAllInventories() {
        return inventoryMapper.findAllInventories();
    }

    @Override
    public List<InventoryResponseDto> searchInventoryByName(String inventoryName) {
        return inventoryMapper.findInventoryByName(inventoryName);
    }
}
