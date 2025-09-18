package com.limnj.noticeboardadmin.file;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileInfoMapper {
    int saveFile(FileInfoRequestDto requestDto);
    int deleteFile(Long imageId);
    FileInfoResponseDto findByType(@Param("typeId") Long typeId, @Param("type") FileType type);
}
