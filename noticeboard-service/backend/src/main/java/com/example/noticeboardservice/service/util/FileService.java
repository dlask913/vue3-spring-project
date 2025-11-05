package com.example.noticeboardservice.service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service @Slf4j
public class FileService {

    public String uploadFile(String imgLocation, MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename(); // image.png
        String savedFileName = createStoreFileName(originalFilename); // 서버에 저장하는 파일명 ( Random )
        try {
            multipartFile.transferTo(new File(getFullPath(imgLocation, savedFileName))); // 저장
        } catch (IOException | IllegalStateException e) {
            log.error("파일 업로드가 실패하였습니다.");
        }
        return savedFileName;
    }

    public String getFullPath(String imgLocation, String filename) {
        return imgLocation + "/" + filename;
    }

    private String  createStoreFileName(String originalFilename) { // 서버에 저장되는 파일명 ( 랜덤 )
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    private String extractExt(String originalFilename) { // 확장자 추출
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    public void deleteFile(String imgLocation, String filePath){ // 파일 삭제
        File deleteFile = new File(getFullPath(imgLocation, filePath));
        if (deleteFile.exists()) {
            deleteFile.delete();
            log.info("기존 파일을 삭제하였습니다.");
        } else {
            log.info("파일이 존재하지 않습니다.");
        }
    }
}
