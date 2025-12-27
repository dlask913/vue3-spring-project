package com.limnj.noticeboardadmin.inventory;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
        try (Workbook workbook = WorkbookFactory.create(multipartFile.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            List<InventoryRequestDto> inventories = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // 헤더 제외
                Row row = sheet.getRow(i);
                if (row == null) continue;

                inventories.add(InventoryRequestDto.builder()
                        .productCode(row.getCell(0).getStringCellValue())
                        .productName(row.getCell(1).getStringCellValue())
                        .orderQty(Long.parseLong(row.getCell(2).getStringCellValue()))
                        .unitPrice(Integer.parseInt(row.getCell(3).getStringCellValue()))
                        .orderDate(row.getCell(4).getStringCellValue())
                        .build());
            }

            inventoryMapper.saveAll(inventories);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
