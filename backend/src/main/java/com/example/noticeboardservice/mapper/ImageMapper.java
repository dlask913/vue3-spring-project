package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.ImageDto;
import com.example.noticeboardservice.dto.ImageType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface ImageMapper {
    int saveImage(ImageDto imageDto);
    int updateImage(ImageDto imageDto);
    int deleteImage(Long imageId);
    ImageDto findByType(@Param("typeId") Long typeId, @Param("type") ImageType type);
}
