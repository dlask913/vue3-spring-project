package com.limnj.noticeboardadmin.file;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FileInfoMapper {
    int saveFile(FileRequestDto requestDto);
    int deleteFile(Long imageId);
    FileResponseDto findByType(@Param("typeId") Long typeId, @Param("type") FileType type);
}
