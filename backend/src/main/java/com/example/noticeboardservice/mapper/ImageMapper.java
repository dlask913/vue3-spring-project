package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.ImageDto;

public interface ImageMapper {
    int saveImage(ImageDto imageDto);
    int updateImage(ImageDto imageDto);
    int deleteImage(Long imageId);
}
