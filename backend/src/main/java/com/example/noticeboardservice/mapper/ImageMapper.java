package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.ImageRequestDto;
import com.example.noticeboardservice.dto.ImageResponseDto;
import com.example.noticeboardservice.dto.ImageType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ImageMapper {
    int saveImage(ImageRequestDto imageRequestDto);
    int deleteImage(Long imageId);
    ImageResponseDto findByType(@Param("typeId") Long typeId, @Param("type") ImageType type);
    void deleteAll();
}
