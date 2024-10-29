package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.ImageRequestDto;
import com.example.noticeboardservice.dto.ImageResponseDto;
import com.example.noticeboardservice.dto.ImageType;
import com.example.noticeboardservice.mapper.ImageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service @Transactional
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageMapper imageMapper;
    private final FileService fileService;

    @Override
    public int saveImage(ImageRequestDto imageRequestDto, MultipartFile multipartFile, String location) {
        String oriImgName = multipartFile.getOriginalFilename();
        String imgName = fileService.uploadFile(location, multipartFile);
        String imgUrl = "/images/" + imageRequestDto.getImageType().toString().toLowerCase() + "/" + imgName;
        Long fileSize = multipartFile.getSize();

        imageRequestDto.updateImage(imgName, oriImgName, imgUrl, fileSize);
        return imageMapper.saveImage(imageRequestDto);
    }

    @Override
    public int deleteImage(Long imageId, String location, String imgName) {
        fileService.deleteFile(location, imgName); // 파일 삭제
        return imageMapper.deleteImage(imageId);
    }

    @Override
    public Optional<ImageResponseDto> findByTypeId(Long typeId, ImageType imageType) {
        return Optional.ofNullable(imageMapper.findByType(typeId, imageType));
    }
}
