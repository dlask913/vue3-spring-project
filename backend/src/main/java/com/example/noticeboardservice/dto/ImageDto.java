package com.example.noticeboardservice.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ImageDto {
    String imgName; // 이미지 파일명
    String oriImgName; // 원본 이미지 파일명
    String imgUrl; // 이미지 조회 경로
    Long fileSize;
    LocalDateTime uploadDate;
    ImageType imageType;

    @Builder
    public ImageDto(String imgName, String oriImgName, String imgUrl, Long fileSize, ImageType imageType) {
        this.imgName = imgName;
        this.oriImgName = oriImgName;
        this.imgUrl = imgUrl;
        this.fileSize = fileSize;
        this.uploadDate = LocalDateTime.now();
        this.imageType = imageType;
    }
}
