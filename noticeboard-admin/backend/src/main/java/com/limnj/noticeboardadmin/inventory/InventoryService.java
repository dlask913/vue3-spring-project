package com.limnj.noticeboardadmin.inventory;


import org.springframework.web.multipart.MultipartFile;

public interface InventoryService {
    void uploadInventoryBulk(MultipartFile multipartFile);
}
