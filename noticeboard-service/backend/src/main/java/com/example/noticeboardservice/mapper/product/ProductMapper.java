package com.example.noticeboardservice.mapper.product;

import com.example.noticeboardservice.dto.product.ProductDetailsResponseDto;
import com.example.noticeboardservice.dto.product.ProductRequestDto;
import com.example.noticeboardservice.dto.product.ProductResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {
    void insertProduct(ProductRequestDto productRequestDto);
    int updateProduct(ProductRequestDto productRequestDto);
    int deleteProduct(Long productId);
    ProductDetailsResponseDto findProduct(Long productId);
    void deleteAll();
    List<ProductResponseDto> findAllProducts();
    List<ProductResponseDto> searchProductsByKeyword(
            @Param("sortKey") String sortKey,
            @Param("order") String order,
            @Param("params") Map<String, String> params
    );
    List<ProductResponseDto> findProductsByCategory(String category);
    List<ProductResponseDto> findProductsByMemberId(Long memberId);
    List<ProductResponseDto> findProductsByDeadline();
}
