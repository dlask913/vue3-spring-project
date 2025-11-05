package com.example.noticeboardservice.mapper.product;

import com.example.noticeboardservice.dto.product.CategoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<CategoryDto> findAllCategories();
}