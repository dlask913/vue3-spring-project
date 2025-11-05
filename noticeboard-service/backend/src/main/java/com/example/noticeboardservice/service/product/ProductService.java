package com.example.noticeboardservice.service.product;

import com.example.noticeboardservice.dto.product.ProductDetailsResponseDto;
import com.example.noticeboardservice.dto.product.ProductRequestDto;
import com.example.noticeboardservice.dto.product.ProductResponseDto;
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
    List<ProductResponseDto> findProductsByMemberId(Long memberId);
    List<ProductResponseDto> findProductsByDeadline();
}
