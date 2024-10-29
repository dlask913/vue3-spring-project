package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.ProductRequestDto;
import com.example.noticeboardservice.dto.ProductResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface ProductService {
    Long insertProduct(ProductRequestDto productRequestDto, MultipartFile productImg);
    int updateProduct(ProductRequestDto productRequestDto);
    int deleteProduct(Long productId);
    ProductResponseDto findProduct(Long productId);
    List<ProductResponseDto> findAllProducts();
}
