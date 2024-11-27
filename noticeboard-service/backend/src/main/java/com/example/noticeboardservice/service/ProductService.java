package com.example.noticeboardservice.service;

import com.example.noticeboardservice.dto.ProductDetailsResponseDto;
import com.example.noticeboardservice.dto.ProductRequestDto;
import com.example.noticeboardservice.dto.ProductResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface ProductService {
    Long insertProduct(ProductRequestDto productRequestDto, MultipartFile productImg);
    int updateProduct(ProductRequestDto productRequestDto);
    int deleteProduct(Long productId);
    ProductDetailsResponseDto findProduct(Long productId);
    List<ProductResponseDto> findAllProducts();
    List<ProductResponseDto> searchProductsByKeyword(String sort, String order, Map<String, String> params);
    List<ProductResponseDto> findProductsByCategory(String category);
}
