package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.ImageDto;
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
    public int saveImage(ImageDto imageDto, MultipartFile multipartFile, String location) {
        String oriImgName = multipartFile.getOriginalFilename();
        String imgName = fileService.uploadFile(location, multipartFile);
        String imgUrl = "/images/" + imageDto.getImageType().toString() + "/" + imgName;
        Long fileSize = multipartFile.getSize();

        imageDto.updateImage(imgName, oriImgName, imgUrl, fileSize);
        return imageMapper.saveImage(imageDto);
    }

    @Override
    public int updateImage(ImageDto imageDto, MultipartFile multipartFile) {
        return 0;
    }

    @Override
    public int deleteImage(Long imageId) {
        return imageMapper.deleteImage(imageId);
    }

    @Override
    public Optional<ImageDto> findByTypeId(Long typeId, ImageType imageType) {
        return Optional.ofNullable(imageMapper.findByType(typeId, imageType));
    }
}
