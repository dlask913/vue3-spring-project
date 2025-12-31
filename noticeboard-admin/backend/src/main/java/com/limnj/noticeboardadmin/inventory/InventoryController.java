package com.limnj.noticeboardadmin.inventory;

import com.limnj.noticeboardadmin.util.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;
    private final PdfService pdfService;

    @PostMapping("/inventories/bulk")
    public ResponseEntity<Void> createInventories(
            @RequestParam("inventoryFile") MultipartFile multipartFile) {
        inventoryService.uploadInventoryBulk(multipartFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/inventories/pdf")
    public ResponseEntity<byte[]> downloadPdf() {

        List<InventoryResponseDto> inventories = inventoryService.findAllInventories();

        byte[] pdfBytes = pdfService.generateInventoryPdf(inventories);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inventory.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
