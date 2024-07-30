package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.ImageDto;
import com.example.noticeboardservice.dto.ImageType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public interface ImageService {
    int saveImage(ImageDto imageDto, MultipartFile multipartFile, String location);
    int updateImage(ImageDto imageDto, MultipartFile multipartFile);
    int deleteImage(Long imageId);
    Optional<ImageDto> findByTypeId(Long typeId, ImageType imageType);
}
