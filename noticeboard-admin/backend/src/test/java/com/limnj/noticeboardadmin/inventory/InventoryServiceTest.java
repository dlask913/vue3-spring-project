package com.limnj.noticeboardadmin.inventory;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest @Transactional
@ActiveProfiles("test")
class InventoryServiceTest {

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InventoryMapper inventoryMapper;

    @Test
    @DisplayName("엑셀 업로드 시 주문 데이터가 저장된다")
    void uploadInventoryBulk_success() throws Exception {
        // given
        MockMultipartFile inventoryFile = createExcelFile();

        // when
        inventoryService.uploadInventoryBulk(inventoryFile);

        // then
        List<InventoryResponseDto> result = inventoryMapper.findAllInventories();

        assertThat(result).hasSize(2);

        InventoryResponseDto first = result.get(0);
        assertThat(first.getProductCode()).isEqualTo("P-001");
        assertThat(first.getProductName()).isEqualTo("키보드");
        assertThat(first.getOrderQty()).isEqualTo(10L);
        assertThat(first.getUnitPrice()).isEqualTo(30000);
        assertThat(first.getOrderDate()).isEqualTo("2025-01-10");
    }

    private MockMultipartFile createExcelFile() throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("sheet1");

        // header
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("productCode");
        header.createCell(1).setCellValue("productName");
        header.createCell(2).setCellValue("orderQty");
        header.createCell(3).setCellValue("unitPrice");
        header.createCell(4).setCellValue("orderDate");

        // row 1
        Row row1 = sheet.createRow(1);
        row1.createCell(0).setCellValue("P-001");
        row1.createCell(1).setCellValue("키보드");
        row1.createCell(2).setCellValue("10");
        row1.createCell(3).setCellValue("30000");
        row1.createCell(4).setCellValue("2025-01-10");

        // row 2
        Row row2 = sheet.createRow(2);
        row2.createCell(0).setCellValue("P-002");
        row2.createCell(1).setCellValue("마우스");
        row2.createCell(2).setCellValue("20");
        row2.createCell(3).setCellValue("15000");
        row2.createCell(4).setCellValue("2025-01-10");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);

        return new MockMultipartFile(
                "inventoryFile",
                "inventory.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                out.toByteArray()
        );
    }
}