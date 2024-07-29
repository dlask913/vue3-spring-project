package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.ImageDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageService {
    int saveImage(ImageDto imageDto, MultipartFile multipartFile, String location);
    int updateImage(ImageDto imageDto, MultipartFile multipartFile);
    int deleteImage(Long imageId);
}
