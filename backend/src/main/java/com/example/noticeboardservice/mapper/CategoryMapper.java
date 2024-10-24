package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.CategoryDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {
    int insertCategory(CategoryDto categoryDto);
    int deleteCategory(Long id);
}