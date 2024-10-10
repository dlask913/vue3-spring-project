package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.ProductRequestDto;
import com.example.noticeboardservice.dto.ProductResponseDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ProductMapper {
    int insertProduct(ProductRequestDto productRequestDto);
    int updateProduct(ProductRequestDto productRequestDto);
    int deleteProduct(Long productId);
    ProductResponseDto findProduct(Long productId);
    void deleteAll();
    List<ProductResponseDto> findAllProducts();
}
