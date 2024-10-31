package com.limnj.noticeboardadmin.image;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface ImageMapper {
    int saveImage(ImageRequestDto imageRequestDto);
    int deleteImage(Long imageId);
    ImageResponseDto findByType(@Param("typeId") Long typeId, @Param("type") ImageType type);
    void deleteAll();
}
