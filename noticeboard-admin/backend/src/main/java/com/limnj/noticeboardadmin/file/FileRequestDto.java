package com.limnj.noticeboardadmin.file;

import com.limnj.noticeboardadmin.image.ImageType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter @NoArgsConstructor
public class FileRequestDto {
    Long id;
    Long typeId;
    String fileName; // 파일 파일명
    String oriFileName; // 원본 파일 파일명
    String fileUrl; // 파일 경로
    Long fileSize;
    LocalDateTime uploadDate;
    FileType fileType;

    @Builder
    public FileRequestDto(Long id, Long typeId, String fileName, String oriFileName, String fileUrl, Long fileSize, LocalDateTime uploadDate, FileType fileType) {
        this.id = id;
        this.typeId = typeId;
        this.fileName = fileName;
        this.oriFileName = oriFileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate;
        this.fileType = fileType;
    }

    public void updateFileInfo(String fileName, String oriFileName, String fileUrl, Long fileSize){
        this.fileName = fileName;
        this.oriFileName = oriFileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
    }
}
