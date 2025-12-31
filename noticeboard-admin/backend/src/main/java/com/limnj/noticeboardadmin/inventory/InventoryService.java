package com.limnj.noticeboardadmin.inventory;


import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InventoryService {
    void uploadInventoryBulk(MultipartFile multipartFile);
    List<InventoryResponseDto> findAllInventories();
}
