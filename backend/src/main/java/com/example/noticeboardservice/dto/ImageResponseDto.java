package com.example.noticeboardservice.dto;


import java.time.LocalDateTime;


public record ImageResponseDto (
        Long id,
        Long typeId,
        String imgName, // 이미지 파일명
        String imgUrl, // 이미지 조회 경로
        LocalDateTime uploadDate,
        String imageType
){
}
