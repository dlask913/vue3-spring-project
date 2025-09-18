package com.limnj.noticeboardadmin.file;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public interface FileInfoService {
    int saveFile(FileInfoRequestDto requestDto, MultipartFile multipartFile, String location);
    int deleteFile(Long imageId, String location, String fileName);
    Optional<FileInfoResponseDto> findByTypeId(Long typeId, FileType fileType);
}
