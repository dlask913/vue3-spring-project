package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.ProductRequestDto;
import com.example.noticeboardservice.dto.ProductResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface ProductMapper {
    void insertProduct(ProductRequestDto productRequestDto);
    int updateProduct(ProductRequestDto productRequestDto);
    int deleteProduct(Long productId);
    ProductResponseDto findProduct(Long productId);
    void deleteAll();
    List<ProductResponseDto> findAllProducts();
    List<ProductResponseDto> searchProductsByKeyword(
            @Param("sortKey") String sortKey,
            @Param("order") String order,
            @Param("params") Map<String, String> params
    );
}
