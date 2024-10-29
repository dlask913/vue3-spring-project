package com.example.noticeboardservice.mapper;

import com.example.noticeboardservice.dto.CategoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    int insertCategory(CategoryDto categoryDto);
    int deleteCategory(Long categoryId);
    void deleteAll();
    CategoryDto findByCategoryId(Long categoryId);
    List<CategoryDto> findAllCategories();
}