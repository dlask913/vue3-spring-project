package com.limnj.noticeboardadmin.image;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public interface ImageService {
    int saveImage(ImageRequestDto imageRequestDto, MultipartFile multipartFile, String location);
    int deleteImage(Long imageId, String location, String imgName);
    Optional<ImageResponseDto> findByTypeId(Long typeId, ImageType imageType);
}
