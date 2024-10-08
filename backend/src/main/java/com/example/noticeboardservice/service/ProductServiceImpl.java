package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.ProductRequestDto;
import com.example.noticeboardservice.dto.ProductResponseDto;
import com.example.noticeboardservice.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;

    @Override
    public int insertProduct(ProductRequestDto productRequestDto) {
        return productMapper.insertProduct(productRequestDto);
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
}
