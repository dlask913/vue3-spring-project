package com.limnj.noticeboardadmin.file;

import java.time.LocalDateTime;

public class FileResponseDto {
    Long id;
    Long typeId;
    String fileName; // 파일명
    String fileUrl; // 파일 경로
    LocalDateTime uploadDate;
    String fileType;
}
