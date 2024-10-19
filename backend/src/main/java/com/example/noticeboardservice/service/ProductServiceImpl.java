package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.ImageRequestDto;
import com.example.noticeboardservice.dto.ImageType;
import com.example.noticeboardservice.dto.ProductRequestDto;
import com.example.noticeboardservice.dto.ProductResponseDto;
import com.example.noticeboardservice.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ImageService imageServiceImpl;

    @Value("${productImgLocation}")
    private String productImgLocation;

    @Override
    public Long insertProduct(ProductRequestDto productRequestDto, MultipartFile productImg) {
        productMapper.insertProduct(productRequestDto);
        if (productImg != null){
            ImageRequestDto imageRequestDto = ImageRequestDto.builder()
                    .typeId(productRequestDto.getId())
                    .imageType(ImageType.PRODUCT)
                    .build();
            imageServiceImpl.saveImage(imageRequestDto, productImg, productImgLocation);
        }
        return productRequestDto.getId();
    }

    @Override
    public int updateProduct(ProductRequestDto productRequestDto) {
        return productMapper.updateProduct(productRequestDto);
    }

    @Override
    public int deleteProduct(Long productId) {
        return productMapper.deleteProduct(productId);
    }

    @Override
    public ProductResponseDto findProduct(Long productId) {
        return productMapper.findProduct(productId);
    }

    @Override
    public List<ProductResponseDto> findAllProducts() {
        return productMapper.findAllProducts();
    }
}
