package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.*;
import com.example.noticeboardservice.mapper.ProductBidHistoryMapper;
import com.example.noticeboardservice.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductBidHistoryMapper productBidHistoryMapper;
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

        // 초기 History 추가
        ProductBidDto initialHistory = ProductBidDto.builder()
                .bidPrice(productRequestDto.getStandardPrice())
                .productId(productRequestDto.getId())
                .customerId(productRequestDto.getOwnerId())
                .build();
        productBidHistoryMapper.addBidHistory(initialHistory);

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

    @Override
    public List<ProductResponseDto> searchProductsByKeyword(String sort, String order, Map<String, String> params) {
        return productMapper.searchProductsByKeyword(sort, order, params);
    }
}
