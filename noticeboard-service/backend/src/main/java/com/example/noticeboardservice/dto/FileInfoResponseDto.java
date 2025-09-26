package com.example.noticeboardservice.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class FileInfoResponseDto {
    Long id;
    Long typeId;
    String fileName; // 파일명
    String fileUrl; // 파일 경로
    LocalDateTime uploadDate;
    String fileType;
}
