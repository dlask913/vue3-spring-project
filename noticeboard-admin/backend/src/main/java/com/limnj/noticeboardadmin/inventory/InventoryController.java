package com.limnj.noticeboardadmin.inventory;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/inventories/bulk")
    public ResponseEntity<Void> createInventories(
            @RequestParam("inventoryFile") MultipartFile multipartFile) {
        inventoryService.uploadInventoryBulk(multipartFile);
        return ResponseEntity.ok().build();
    }
}
