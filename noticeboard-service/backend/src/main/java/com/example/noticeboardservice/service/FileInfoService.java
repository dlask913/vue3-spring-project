package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.FileInfoRequestDto;
import com.example.noticeboardservice.dto.FileInfoResponseDto;
import com.example.noticeboardservice.dto.FileType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public interface FileInfoService {
    int saveFile(FileInfoRequestDto requestDto, MultipartFile multipartFile, String location);
    int deleteFile(Long imageId, String location, String fileName);
    Optional<FileInfoResponseDto> findByTypeId(Long typeId, FileType fileType);
}
